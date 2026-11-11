package at.yedel.ghissue.data

data class Repo(val owner: String, val repo: String) {
    override fun toString(): String {
        return "$owner/$repo"
    }
}