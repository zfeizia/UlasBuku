import re

path_profile = r"app\src\main\java\com\pemmob\ulasbuku\ui\screens\ProfileScreen.kt"
with open(path_profile, 'r', encoding='utf-8') as f:
    text_profile = f.read()

# Replace val userReviews by viewModel.userReviews.collectAsState()
text_profile = text_profile.replace('val userReviews by viewModel.userReviews.collectAsState()', 'val userHistory by viewModel.userHistory.collectAsState()')
text_profile = text_profile.replace('StatItem(count = "${userReviews.size}", label = "Ulasan")', 'StatItem(count = "${userHistory.size}", label = "Riwayat")')
text_profile = text_profile.replace('text = "${userReviews.size} Ulasan",', 'text = "${userHistory.size} Riwayat Ulasan & Balasan",')
text_profile = text_profile.replace('if (userReviews.isEmpty()) {', 'if (userHistory.isEmpty()) {')

list_items_block = """
                items(
                    items = userReviews,
                    key = { (_, review) -> "review_${review.id}" }
                ) { (book, review) ->
                    ProfileReviewCard(book = book, review = review, onClick = { onBookClick(book) })
                }
"""

replacement_items = """
                items(
                    items = userHistory,
                    key = { item -> 
                        when (item) {
                            is com.pemmob.ulasbuku.data.model.UserHistoryItem.ReviewItem -> "review_${item.review.id}"
                            is com.pemmob.ulasbuku.data.model.UserHistoryItem.ReplyItem -> "reply_${item.reply.id}"
                        }
                    }
                ) { item ->
                    when (item) {
                        is com.pemmob.ulasbuku.data.model.UserHistoryItem.ReviewItem -> {
                            ProfileReviewCard(book = item.book, review = item.review, onClick = { onBookClick(item.book) })
                        }
                        is com.pemmob.ulasbuku.data.model.UserHistoryItem.ReplyItem -> {
                            ProfileReplyCard(book = item.book, review = item.originalReview, reply = item.reply, onClick = { onBookClick(item.book) })
                        }
                    }
                }
"""

# Find exact match or use regex
if 'items(\n                    items = userReviews,' in text_profile:
    text_profile = re.sub(r'items\([\s\S]*?ProfileReviewCard\([\s\S]*?\}\s*\}', replacement_items.strip() + '\n                }', text_profile)

# Also need to add ProfileReplyCard composable at the bottom
reply_card_composable = """
@Composable
private fun ProfileReplyCard(book: com.pemmob.ulasbuku.data.model.Book, review: com.pemmob.ulasbuku.data.model.Review, reply: com.pemmob.ulasbuku.data.model.Reply, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(1.5.dp, BorderDark, RoundedCornerShape(18.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = PureWhite)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(book.title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                Text("Membalas", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Medium)
            }
            Text("Membalas ulasan dari ${review.reviewerName}", color = TextSecondary, fontSize = 11.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, modifier = Modifier.padding(top = 4.dp))
            Text(reply.replyText, color = TextPrimary, fontSize = 12.sp, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis, modifier = Modifier.padding(top = 4.dp))
        }
    }
}
"""

if 'fun ProfileReplyCard' not in text_profile:
    text_profile = text_profile + "\n" + reply_card_composable

with open(path_profile, 'w', encoding='utf-8') as f:
    f.write(text_profile)

print("ProfileScreen updated")
