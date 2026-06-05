package com.pamt.swarabox.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.pamt.swarabox.R
import com.pamt.swarabox.ui.navigation.Home
import com.pamt.swarabox.ui.navigation.Profile
import com.pamt.swarabox.ui.navigation.Upload

@Composable
fun AppNavigationBar(
    navController: NavController,
    currentDestination: NavDestination?,
    containerColor: Color,
    contentColor: Color,
    selectedIconColor: Color,
    unselectedIconColor: Color,
    indicatorColor: Color,
) {
    NavigationBar(
        containerColor = containerColor,
        contentColor = contentColor,
    ) {
        NavigationBarItem(
            selected = currentDestination?.route?.contains("Home") == true,
            onClick = {
                navController.navigate(Home()) {
                    popUpTo<Home> { inclusive = false }
                }
            },
            icon = {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.home),
                    contentDescription = "Home",
                )
            },
            label = { Text("Home", color = Color.White) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedIconColor,
                unselectedIconColor = unselectedIconColor,
                indicatorColor = indicatorColor
            ),
            modifier = Modifier.size(24.dp),
        )
        NavigationBarItem(
            selected = currentDestination?.route?.contains("Upload") == true,
            onClick = {
                navController.navigate(Upload) {
                    popUpTo<Home> { inclusive = false }
                }
            },
            icon = {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.upload),
                    contentDescription = "Upload",
                    modifier = Modifier.size(20.dp),
                )
            },
            label = { Text("Upload", color = Color.White) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedIconColor,
                unselectedIconColor = unselectedIconColor,
                indicatorColor = indicatorColor
            ),
            modifier = Modifier.size(24.dp),
        )

        NavigationBarItem(
            selected = currentDestination?.route?.contains("Profile") == true,
            onClick = {
                navController.navigate(Profile) {
                    popUpTo<Home> { inclusive = false }
                }
            },
            icon = {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.profile),
                    contentDescription = "Profile",
                    modifier = Modifier.size(20.dp),
                )
            },
            label = { Text("Profile", color = Color.White) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedIconColor,
                unselectedIconColor = unselectedIconColor,
                indicatorColor = indicatorColor
            ),
            modifier = Modifier.size(24.dp),
        )

    }
}