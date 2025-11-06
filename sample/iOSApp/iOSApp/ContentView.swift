import SwiftUI
import PostFramework
import Combine

@MainActor
final class PostListViewModel: ObservableObject {
    @Published var posts: [Post] = []
    @Published var isLoading = false
    @Published var errorMessage: String?

    func loadPosts() {
        let repository = (KoinManager.shared.getPostRepository() as! PostRepository)
        let postsFlow = repository.getAllPost()

        FlowExtKt.collectResult(
            postsFlow,
            onSuccess: { [weak self] postList in
                self?.posts = postList as! [Post]
                self?.isLoading = false
            },
            onLoading: { [weak self] in
                self?.isLoading = true
            },
            onError: { [weak self] errorMsg in
                self?.errorMessage = errorMsg
                self?.isLoading = false
            }
        )
    }
}

struct ContentView: View {
    @StateObject private var viewModel = PostListViewModel()

    var body: some View {
        ZStack {
            if viewModel.isLoading {
                ProgressView()
            } else if !viewModel.posts.isEmpty {
                List(viewModel.posts, id: \.id) { post in
                    Text(post.title)
                }
            } else if let error = viewModel.errorMessage {
                Text("Error: \(error)").foregroundColor(.red)
            }
        }
        .onAppear {
            viewModel.loadPosts()
        }
    }
}

#Preview {
    ContentView()
}
