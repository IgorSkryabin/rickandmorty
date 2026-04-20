package com.example.rickandmorty.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil3.compose.AsyncImage
import com.example.rickandmorty.R
import com.example.rickandmorty.models.CharacterModel
import com.example.rickandmorty.models.Location
import com.example.rickandmorty.models.Origin
import com.example.rickandmorty.ui.theme.RickAndMortyTheme
import com.example.rickandmorty.viewModels.CharsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable
import java.io.File

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RickAndMortyTheme {
                RickAndMortyNavHost()
            }
        }
    }
}

val getSchwiftyFont = FontFamily(Font(R.font.get_schwifty))

@Serializable
object CharsListDest
@Serializable
data class CharInfoDest(val charId: Int? = null)

private val aChar =
    CharacterModel(
        0,
        "name of person",
        "status",
        "species",
        "type",
        "gender",
        Origin("origin", "url"),
        Location("location", "url"),
        "image",
        listOf(),
        "url",
        "created"
    )

@Composable
fun RickAndMortyNavHost() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = CharsListDest) {
        composable<CharsListDest> {
            CharactersList(
                onNavigateToCharInfo = { charId -> navController.navigate(
                    route = CharInfoDest(charId),
                ) }
            )
        }
        composable<CharInfoDest> { backStackEntry ->
            val charInfoDest = backStackEntry.toRoute<CharInfoDest>()
            CharacterInfo(
                charInfoDest.charId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun CharactersList(
    onNavigateToCharInfo: (charId: Int?) -> Unit = {},
    charsViewModel: CharsViewModel = hiltViewModel()
) {
    val charsList by charsViewModel.state.collectAsState()
    val errorMsg by charsViewModel.stateErr.collectAsState()
    val isRefreshing by charsViewModel.isRefreshing.collectAsState()
    val isLoading by charsViewModel.isLoading.collectAsState()

    CharactersListScreen(
        charsList = charsList,
        isRefreshing = isRefreshing,
        isLoading = isLoading,
        errorMsg = errorMsg.message.toString(),
        onRefresh = { charsViewModel.onRefresh() },
        onSearch = { charsViewModel.searchByTextField(it) },
        onNavigateToCharInfo = onNavigateToCharInfo,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun CharactersListScreen(
    charsList: List<CharacterModel> = listOf(aChar, aChar, aChar, aChar, aChar, aChar),
    isRefreshing: Boolean = false,
    isLoading: Boolean = false,
    errorMsg: String = "",
    onRefresh: () -> Unit = {},
    onSearch: (String) -> Unit = {},
    onNavigateToCharInfo: (charId: Int?) -> Unit = {},
) {


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = topAppBarColors(
                    containerColor = colorResource(R.color.greenVeryDark),
                    titleContentColor = colorResource(R.color.white),
                ),
                title = {
                    Text(stringResource(R.string.app_name), fontFamily = getSchwiftyFont)
                }
            )
        },
        containerColor = colorResource(R.color.greenVeryDark),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {  },
                containerColor = colorResource(R.color.greenDarkEmerald),
                shape = CircleShape
            ) {
                Icon(painterResource(R.drawable.ic_menu), contentDescription = "Filters", tint = Color.White)
            }
        },
    ) { innerPadding ->

        Column(
            modifier = Modifier.padding(innerPadding),
        ) {
            var textFieldValue by remember { mutableStateOf("") }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = textFieldValue,
                    onValueChange = { textFieldValue = it },
                    label = { Text(
                        text = "Search",
                        color = Color.White,
                        fontFamily = getSchwiftyFont
                    ) },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colorResource(R.color.greenVeryDark),
                        unfocusedContainerColor = colorResource(R.color.greenVeryDark),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                    ),
                )
                IconButton(onClick = { onSearch(textFieldValue) }) {
                    Icon(
                        painterResource(R.drawable.ic_search),
                        contentDescription = "Search",
                        tint = Color.White
                    )
                }
            }
            val pullRefreshState = rememberPullRefreshState(
                refreshing = isRefreshing,
                onRefresh = onRefresh
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pullRefresh(pullRefreshState),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.Green)
                }
                if (charsList.isEmpty() && errorMsg.isNotBlank()) {
                    Row(Modifier.padding(horizontal = 5.dp)) {
                        Text(
                            text = errorMsg,
                            color = Color.White,
                            fontFamily = getSchwiftyFont,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    verticalItemSpacing = 5.dp,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(charsList) { charModel ->
                        Card(
                            colors = CardDefaults
                                .cardColors(
                                    containerColor = colorResource(R.color.greenDarkEmerald),
                                    contentColor = Color.White
                                ),
                            shape = RoundedCornerShape(35.dp),
                            modifier = Modifier.width(180.dp).wrapContentHeight(),
                            onClick = { onNavigateToCharInfo(charModel.id) }
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(contentAlignment = Alignment.BottomEnd) {
                                    if (LocalInspectionMode.current) {
                                        Image(
                                            painter =
                                                painterResource(R.drawable.rick_and_morty_prev),
                                            contentDescription = "This is ${charModel.name}",
                                            modifier = Modifier.height(180.dp),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        val localFile = File(
                                            LocalContext.current.filesDir,
                                            "${charModel.id}.jpg"
                                        )
                                        AsyncImage(
                                            model =
                                            if (localFile.exists())
                                                localFile
                                            else
                                                charModel.image,
                                            contentDescription = "This is ${charModel.name}",
                                            modifier = Modifier.height(180.dp),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                    Box(
                                        Modifier
                                            .size(40.dp, 20.dp)
                                            .background(
                                                color = colorResource(R.color.greenDark),
                                                shape = RoundedCornerShape(topStart = 15.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            Modifier.padding(end = 10.dp).size(10.dp).background(
                                                shape = CircleShape,
                                                color = when (charModel.status?.lowercase()) {
                                                    "alive" -> Color.Green
                                                    "dead" -> Color.Red
                                                    else -> Color.Gray
                                                }
                                            )
                                        )
                                    }
                                }
                                Spacer(Modifier.height(20.dp))
                                Text(
                                    "${charModel.name}",
                                    fontFamily = getSchwiftyFont,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(10.dp))
                                Row(
                                    Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        Modifier
                                            .fillMaxWidth()
                                            .weight(1f),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Text(
                                            "${charModel.species}",
                                            fontFamily = getSchwiftyFont,
                                            textAlign = TextAlign.End
                                        )
                                    }

                                    Text(
                                        "|",
                                        fontFamily = getSchwiftyFont,
                                        modifier = Modifier.padding(horizontal = 3.dp)
                                    )
                                    Row(
                                        Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                    ) {
                                        Text(
                                            "${charModel.origin?.name}",
                                            fontFamily = getSchwiftyFont,
                                        )
                                    }
                                }
                                Spacer(Modifier.height(20.dp))
                            }
                        }
                    }
                }
                androidx.compose.material.pullrefresh.PullRefreshIndicator(
                    refreshing = isRefreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter),
                    backgroundColor = colorResource(R.color.greenDarkEmerald),
                    contentColor = Color.White
                )
            }
        }
    }
}

@Composable
fun CharacterInfo(
    charId: Int?,
    onNavigateBack: () -> Unit = {},
    charsViewModel: CharsViewModel = hiltViewModel()
) {
    val charsList by charsViewModel.state.collectAsState()
    val charModel = charsList.find { it.id == charId }
    if (charModel != null) {
        CharacterInfoScreen(charModel, onNavigateBack)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterInfoScreen(
    charModel: CharacterModel = aChar,
    onNavigateBack: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painterResource(R.drawable.ic_arrow_back),
                            contentDescription = "Back",
                            tint = colorResource(R.color.white)
                        )
                    }
                },
                title = {
                    Text(
                        stringResource(R.string.app_name),
                        fontFamily = FontFamily(Font(R.font.get_schwifty))
                    )
                },
                colors = topAppBarColors(
                    containerColor = colorResource(R.color.greenVeryDark),
                    titleContentColor = colorResource(R.color.white),
                )
            )
        },
        containerColor = colorResource(R.color.greenVeryDark),
    ) { innerPadding ->
        Row(Modifier.padding(innerPadding).fillMaxSize()) {
            if (LocalInspectionMode.current) {
                Image(
                    painter = painterResource(R.drawable.rick_and_morty_prev),
                    contentDescription = "This is ${charModel.name}",
                    modifier = Modifier.width(180.dp)
                )
            } else {
                val localFile = File(LocalContext.current.filesDir, "${charModel.id}.jpg")
                AsyncImage(
                    model = if (localFile.exists()) localFile else charModel.image,
                    contentDescription = "This is ${charModel.name}",
                    modifier = Modifier.width(180.dp),
                    contentScale = ContentScale.FillWidth
                )
            }
            Column {
                val textColor = colorResource(R.color.white)
                Text("Name: ${charModel.name}", color = textColor, fontFamily = getSchwiftyFont)
                Text("Status: ${charModel.status}", color = textColor, fontFamily = getSchwiftyFont)
                Text("Species: ${charModel.species}", color = textColor, fontFamily = getSchwiftyFont)
                Text("Type: ${charModel.type}", color = textColor, fontFamily = getSchwiftyFont)
                Text("Gender: ${charModel.gender}", color = textColor, fontFamily = getSchwiftyFont)
                Text("Origin: ${charModel.origin?.name}", color = textColor, fontFamily = getSchwiftyFont)
                Text("Location: ${charModel.location?.name}", color = textColor, fontFamily = getSchwiftyFont)
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun CharactersListScreenPreview() {
    RickAndMortyTheme {
        CharactersListScreen()
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun CharacterInfoScreenPreview() {
    RickAndMortyTheme {
        CharacterInfoScreen()
    }
}

