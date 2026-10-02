import re

path_vm = r"app\src\main\java\com\pemmob\ulasbuku\ui\viewmodel\BookViewModel.kt"
with open(path_vm, 'r', encoding='utf-8') as f:
    text_vm = f.read()

# Replace userReviews with userHistory
# We still might want userReviews around, or just replace it. Let's replace it.
text_vm = text_vm.replace('private val _userReviews = MutableStateFlow<List<Pair<Book, Review>>>(emptyList())',
                          'private val _userHistory = MutableStateFlow<List<com.pemmob.ulasbuku.data.model.UserHistoryItem>>(emptyList())')
text_vm = text_vm.replace('val userReviews: StateFlow<List<Pair<Book, Review>>> = _userReviews.asStateFlow()',
                          'val userHistory: StateFlow<List<com.pemmob.ulasbuku.data.model.UserHistoryItem>> = _userHistory.asStateFlow()')
text_vm = text_vm.replace('_userReviews.value = emptyList()', '_userHistory.value = emptyList()')
text_vm = text_vm.replace('_userReviews.value = repository.getUserReviews(user.name)', '_userHistory.value = repository.getUserHistory(user.name)')

with open(path_vm, 'w', encoding='utf-8') as f:
    f.write(text_vm)

print("ViewModel updated")
