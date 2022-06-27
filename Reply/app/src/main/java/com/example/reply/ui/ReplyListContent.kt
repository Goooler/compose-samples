/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.reply.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.reply.R
import com.example.reply.data.Email
import com.example.reply.ui.components.EmailDetailAppBar
import com.example.reply.ui.components.ReplyEmailListItem
import com.example.reply.ui.components.ReplyEmailThreadItem
import com.example.reply.ui.components.ReplySearchBar
import com.example.reply.ui.utils.ReplyContentType
import com.example.reply.ui.utils.ReplyNavigationType

@Composable
fun ReplyInboxScreen(
    contentType: ReplyContentType,
    replyHomeUIState: ReplyHomeUIState,
    navigationType: ReplyNavigationType,
    closeDetailScreen: () -> Unit,
    navigateToDetail: (Long) -> Unit
) {
    val emailLazyListState = rememberLazyListState()

    if (contentType == ReplyContentType.LIST_AND_DETAIL) {
        ReplyListAndDetailContent(
            replyHomeUIState = replyHomeUIState,
            emailLazyListState = emailLazyListState,
            modifier = Modifier.fillMaxSize(),
            navigateToDetail = navigateToDetail
        )
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            ReplyListOnlyContent(
                replyHomeUIState = replyHomeUIState,
                emailLazyListState = emailLazyListState,
                modifier = Modifier.fillMaxSize(),
                closeDetailScreen = closeDetailScreen,
                navigateToDetail = navigateToDetail
            )
            // When we have bottom navigation we show FAB at the bottom end.
            if (navigationType == ReplyNavigationType.BOTTOM_NAVIGATION) {
                LargeFloatingActionButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(id = R.string.edit),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun ReplyListOnlyContent(
    replyHomeUIState: ReplyHomeUIState,
    emailLazyListState: LazyListState,
    modifier: Modifier = Modifier,
    closeDetailScreen: () -> Unit,
    navigateToDetail: (Long) -> Unit
) {
    if (replyHomeUIState.selectedEmail != null && replyHomeUIState.showDetailScreen) {
        BackHandler {
            closeDetailScreen.invoke()
        }
        ReplyEmailDetail(email = replyHomeUIState.selectedEmail) {
            closeDetailScreen.invoke()
        }
    } else {
        LazyColumn(modifier = modifier, state = emailLazyListState) {
            item {
                ReplySearchBar(modifier = Modifier.fillMaxWidth())
            }
            items(replyHomeUIState.emails) { email ->
                ReplyEmailListItem(email = email) { emailId ->
                    navigateToDetail.invoke(emailId)
                }
            }
        }
    }
}

@Composable
fun ReplyListAndDetailContent(
    replyHomeUIState: ReplyHomeUIState,
    emailLazyListState: LazyListState,
    modifier: Modifier = Modifier,
    navigateToDetail: (Long) -> Unit
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        LazyColumn(modifier = modifier.weight(1f), state = emailLazyListState) {
            item {
                ReplySearchBar(modifier = Modifier.fillMaxWidth())
            }
            items(replyHomeUIState.emails) { email ->
                ReplyEmailListItem(email = email, isSelectable = true, isSelected = replyHomeUIState.selectedEmail?.id  == email.id) {
                    navigateToDetail.invoke(it)
                }
            }
        }
        ReplyEmailDetail(
            modifier = Modifier.weight(1f),
            isFullScreen = false,
            email = replyHomeUIState.selectedEmail ?: replyHomeUIState.emails.first()
        ) {}
    }
}

@Composable
fun ReplyEmailDetail(
    email: Email,
    isFullScreen: Boolean = true,
    modifier: Modifier = Modifier.fillMaxSize(),
    onBackPressed: () -> Unit
) {
    LazyColumn(modifier = modifier.background(MaterialTheme.colorScheme.inverseOnSurface)) {
        item { EmailDetailAppBar(email, isFullScreen) {
            onBackPressed.invoke()
        } }
        items(email.threads) { email ->
            ReplyEmailThreadItem(email = email)
        }
    }
}