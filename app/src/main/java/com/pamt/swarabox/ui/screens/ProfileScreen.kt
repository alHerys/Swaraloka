package com.pamt.swarabox.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.pamt.swarabox.R
import com.pamt.swarabox.data.model.SongModel
import com.pamt.swarabox.data.model.UserModel
import com.pamt.swarabox.ui.components.AppButton
import com.pamt.swarabox.ui.components.CircleContainer
import com.pamt.swarabox.ui.components.SongTile
import com.pamt.swarabox.ui.navigation.About
import com.pamt.swarabox.ui.navigation.EditProfile
import com.pamt.swarabox.ui.navigation.PlayMusic
import com.pamt.swarabox.viewmodel.auth.AuthViewModel
import com.pamt.swarabox.viewmodel.profile.ProfileUiState
import com.pamt.swarabox.viewmodel.profile.ProfileViewModel

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    authViewModel: AuthViewModel,
    navController: NavController
) {
    val mySongs by profileViewModel.mySongs.collectAsStateWithLifecycle()
    val isRefreshingSongs by profileViewModel.isRefreshingSongs.collectAsStateWithLifecycle()
    val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()

    val lottieComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.emptymysong)
    )

    val lottieProgress by animateLottieCompositionAsState(
        composition = lottieComposition,
        iterations = LottieConstants.IterateForever
    )

    ProfileContent(
        user = (uiState as? ProfileUiState.Success)?.user ?: UserModel.dummy,
        listMySong = mySongs,
        isRefreshing = isRefreshingSongs,
        lottieComposition = lottieComposition,
        lottieProgress = lottieProgress,
        onRefresh = { profileViewModel.fetchMySongs(forceRefresh = true) },
        onLogout = {
            profileViewModel.resetUiState()
            authViewModel.resetFormState()
            authViewModel.logout()
        },
        onNavigateToAbout = {
            navController.navigate(About)
        },
        onNavigateToEdit = { currentUser ->
            navController.navigate(EditProfile(currentUser))
        },
        onNavigateToPlay = { song ->
            navController.navigate(PlayMusic(song))
        }
    )
}

@Composable
fun ProfileContent(
    user: UserModel,
    listMySong: List<SongModel>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToEdit: (UserModel) -> Unit,
    onNavigateToPlay: (SongModel) -> Unit,
    lottieComposition: LottieComposition?,
    lottieProgress: Float,
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(24.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    CircleContainer(
                        size = 38.dp,
                        backgroundColor = Color(0xFF343434),
                        onClick = { onNavigateToEdit(user) }
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.edit),
                            tint = Color.White,
                            contentDescription = null
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircleContainer(
                        size = 120.dp,
                        backgroundColor = Color.Gray
                    ) {
                        if (user.avatarUrl != null) {
                            AsyncImage(
                                model = user.avatarUrl,
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            val initials = user.name.split(" ").filter { it.isNotBlank() }.take(2)
                                .joinToString("") { it.take(1).uppercase() }
                            Text(
                                text = initials,
                                style = MaterialTheme.typography.headlineLarge,
                                color = Color.White
                            )
                        }
                    }

                    Text(
                        text = user.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight(500),
                        color = Color(0xFFFFFFFF),
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Text(
                        text = user.email,
                        fontSize = 14.sp,
                        fontWeight = FontWeight(500),
                        color = Color(0xFF747474),
                    )

                    Spacer(Modifier.height(40.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            imageVector = ImageVector.vectorResource(
                                id = R.drawable.library_music
                            ),
                            contentDescription = null,
                            tint = Color.White,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "My Collections",
                            fontSize = 18.sp,
                            fontWeight = FontWeight(500),
                            color = Color(0xFFFFFFFF),
                        )
                    }
                    Spacer(Modifier.height(16.dp))

                    if (listMySong.isEmpty()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            LottieAnimation(
                                composition = lottieComposition,
                                progress = { lottieProgress },
                                modifier = Modifier.size(200.dp)
                            )
                            Spacer(modifier = Modifier.height(18.dp))
                            Text(
                                text = "Your Collection is empty",
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )

                            Spacer(Modifier.height(16.dp))
                        }
                    }
                }
            }

            items(listMySong) { song ->
                SongTile(
                    song = song,
                    onClick = { onNavigateToPlay(song) },
                )
                Spacer(Modifier.height(16.dp))
            }

            item {
                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = onNavigateToAbout,
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0x33FEFEFE)
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.about),
                            tint = Color.White,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(16.dp))
                        Text(
                            text = "About Swaraloka",
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight(500),
                            color = Color.White,
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                AppButton(
                    onClick = onLogout,
                    text = "Logout",
                    containerColor = Color(0xFFF04444),
                    textColor = Color.White,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
