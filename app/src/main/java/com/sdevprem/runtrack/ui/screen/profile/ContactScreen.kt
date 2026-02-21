package com.sdevprem.runtrack.ui.screen.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sdevprem.runtrack.R
import com.sdevprem.runtrack.ui.common.compose.component.RunTrackTopBar
import androidx.compose.ui.res.stringResource

@Composable
fun ContactScreen(
    navController: NavController
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            RunTrackTopBar(
                title = stringResource(R.string.our_contact),
                navController = navController
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(24.dp)
        ) {
            ContactOptionCard(
                iconRes = R.drawable.ic_telephone_receiver,
                title = stringResource(R.string.email_us),
                detail = "sdevprem@gmail.com",
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:sdevprem@gmail.com")
                    }
                    context.startActivity(Intent.createChooser(intent, "Send Email"))
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            ContactOptionCard(
                iconRes = R.drawable.ic_profile, // Placeholder for GitHub/Social
                title = stringResource(R.string.follow_on_github),
                detail = "github.com/sdevprem",
                onClick = {
                     val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/sdevprem"))
                     context.startActivity(intent)
                }
            )
        }
    }
}

@Composable
fun ContactOptionCard(
    iconRes: Int,
    title: String,
    detail: String,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
             Icon(
                imageVector = ImageVector.vectorResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.size(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = detail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
