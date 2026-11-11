package at.yedel.ghissue.data

import com.mojang.brigadier.LiteralMessage
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.util.concurrent.CompletableFuture

object RepoArgumentType : ArgumentType<Repo> {
    val EXAMPLES = listOf("Yedelo/ghissue", "Polyfrost/PolySprint", "torvalds/linux", "Mojang/brigadier")

    override fun parse(reader: StringReader?): Repo? {
        try {
            val string = reader?.readString() ?: return null
            if (string.first() == '/') {
                throw DynamicCommandExceptionType { LiteralMessage("$it has no owner") }.create(string)
            } else if (string.last() == '/') {
                throw DynamicCommandExceptionType { LiteralMessage("$it has no repo") }.create(string)
            } else if (!string.contains("/")) {
                throw DynamicCommandExceptionType { LiteralMessage("$it has no separator") }.create(string)
            }
            val indexOfSlash = string.indexOf('/')
            val owner = string.substring(0, indexOfSlash)
            val repo = string.substring(indexOfSlash + 1)
            return Repo(owner, repo)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun <S : Any?> listSuggestions(
        context: CommandContext<S?>?,
        builder: SuggestionsBuilder?
    ): CompletableFuture<Suggestions?>? {
        return builder.
    }

    override fun getExamples(): Collection<String?> {
        return EXAMPLES
    }
}