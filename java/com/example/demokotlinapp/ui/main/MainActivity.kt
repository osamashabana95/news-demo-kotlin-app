package com.example.demokotlinapp.ui.main

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.demokotlinapp.data.Article
import com.example.demokotlinapp.ui.theme.DemoKotlinAppTheme
import com.example.demokotlinapp.ui.viewmodel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DemoKotlinAppTheme {
                AppNavigation()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("news") {
            val viewModel = hiltViewModel<NewsViewModel>()
            NewsScreen(viewModel, navController)
        }
        composable("details") { backStackEntry ->

            DetailsScreen(navController)

        }
    }
}

@Composable
fun SplashScreen(navController: NavHostController) {
    LaunchedEffect(key1 = true) {
        delay(3000)
        navController.navigate("news")
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Osama News Demo App",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(newsViewModel: NewsViewModel, navController: NavHostController) {
    val newsList by newsViewModel.newsList.collectAsState()
    val isLoading by newsViewModel.isLoading.collectAsState()
    val error by newsViewModel.error.collectAsState()
    Scaffold(
        topBar = { TopAppBar(title = { Text("News") }) }
    ) { paddingValues ->
        if (isLoading) {
            // Show loading indicator
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading...")
            }
        } else if (error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Error: $error")
            }
        } else {
            NewsList(newsList, navController, paddingValues)
        }
    }
}

@Composable
fun NewsList(
    newsList: List<Article>,
    navController: NavHostController,
    paddingValues: androidx.compose.foundation.layout.PaddingValues
) {
    LazyColumn(modifier = Modifier.padding(paddingValues)) {
        items(newsList) { newsArticle ->
            NewsCard(newsArticle, navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsCard(newsArticle: Article, navController: NavHostController) {

    val context = LocalContext.current
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("news_prefs", Context.MODE_PRIVATE)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                sharedPreferences
                    .edit()
                    .putString("titleArticle", newsArticle.title)
                    .apply()
                sharedPreferences
                    .edit()
                    .putString("descriptionArticle", newsArticle.description)
                    .apply()
                sharedPreferences
                    .edit()
                    .putString("authorArticle", newsArticle.author)
                    .apply()
                sharedPreferences
                    .edit()
                    .putString("imageUrlArticle", newsArticle.imageUrl)
                    .apply()

                navController.navigate("details")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                newsArticle.author ?: "Unknown Author",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(newsArticle.title, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun DetailsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("news_prefs", Context.MODE_PRIVATE)

    val newsArticle = produceState<Article?>(initialValue = null) {
        val title = sharedPreferences.getString("titleArticle", null) ?: "Unknown Title"
        val description =
            sharedPreferences.getString("descriptionArticle", null) ?: "No Description Available"
        val author = sharedPreferences.getString("authorArticle", null) ?: "Unknown Author"
        val imageUrl = sharedPreferences.getString("imageUrlArticle", null)
            ?: "https://placehold.co/600x400/FFFFFF/CA4757/png?text=Breaking+News"

        if (title != null && description != null && author != null && imageUrl != null) {
            value = Article(title, description, author, imageUrl)
        }
    }.value

    if (newsArticle != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            AsyncImage(
                model = newsArticle.imageUrl,
                contentDescription = "News Image",
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = newsArticle.title,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = "By ${newsArticle.author}",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = newsArticle.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
