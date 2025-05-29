package com.nmheir.kanicard.extensions.md

import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.nmheir.kanicard.ui.theme.KaniTheme
import org.commonmark.Extension
import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.footnotes.FootnotesExtension
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension
import org.commonmark.ext.image.attributes.ImageAttributesExtension
import org.commonmark.ext.ins.InsExtension
import org.commonmark.ext.task.list.items.TaskListItemsExtension
import org.commonmark.node.CustomNode
import org.commonmark.node.Node
import org.commonmark.node.Text
import org.commonmark.node.Visitor
import org.commonmark.parser.Parser
import org.commonmark.parser.delimiter.DelimiterProcessor
import org.commonmark.parser.delimiter.DelimiterRun
import org.commonmark.renderer.NodeRenderer
import org.commonmark.renderer.html.HtmlNodeRendererContext
import org.commonmark.renderer.html.HtmlRenderer
import org.commonmark.renderer.text.TextContentNodeRendererContext
import org.commonmark.renderer.text.TextContentRenderer
import timber.log.Timber

/**
 * AST node for parameters in the format {{paramName}}
 */
class ParameterNode(val name: String) : CustomNode() {
    override fun accept(visitor: Visitor?) {
        super.accept(visitor)
    }
}

/**
 * Extension for parsing parameters in the format {{paramName}}
 */
class ParameterInlineParserExtension : Parser.ParserExtension {
    override fun extend(parserBuilder: Parser.Builder) {
        parserBuilder.customDelimiterProcessor(ParameterDelimiterProcessor())
    }

    private class ParameterDelimiterProcessor : DelimiterProcessor {
        override fun getOpeningCharacter(): Char = '{'
        override fun getClosingCharacter(): Char = '}'
        override fun getMinLength(): Int = 2

        override fun process(openingRun: DelimiterRun?, closingRun: DelimiterRun?): Int {
            if (openingRun == null || closingRun == null) {
                return 0
            }

            // Kiểm tra cả hai delimiter có độ dài tối thiểu là 2 (để khớp với "{{" và "}}")
            if (openingRun.length() < 2 || closingRun.length() < 2) {
                return 0
            }

            // Truy cập text giữa hai delimiter
            val opener = openingRun.opener
            val closer = closingRun.closer

            if (opener != null && closer != null) {
                // Lấy text nodes giữa opener và closer
                var current = opener.next
                val textBuilder = StringBuilder()

                while (current != null && current != closer) {
                    if (current is Text) {
                        textBuilder.append(current.literal)
                    }
                    val next = current.next
                    current.unlink()
                    current = next
                }

                val paramName = textBuilder.toString().trim()
                if (paramName.isNotEmpty()) {
                    val paramNode = ParameterNode(paramName)
                    opener.insertAfter(paramNode)
                    return 2  // Số lượng delimiter đã sử dụng ở mỗi bên
                }
            }

            return 0
        }
    }
}

/**
 * HTML renderer for parameter nodes
 */
class ParameterHtmlNodeRenderer(
    private val context: HtmlNodeRendererContext,
    private val parameters: Map<String, String>
) : NodeRenderer {

    // 1. Khai báo loại Node mà renderer này sẽ xử lý
    override fun getNodeTypes(): Set<Class<out Node>> =
        setOf(ParameterNode::class.java)

    // 2. Khi gặp một Node, render thành HTML/text tương ứng
    override fun render(node: Node) {
        if (node is ParameterNode) {
            // Lấy tên tham số
            val paramName = node.name
            // Tìm giá trị trong map; nếu không tìm thấy, giữ nguyên "{{paramName}}"
            val value = parameters[paramName] ?: "{{$paramName}}"
            // Xuất ra text đã thay thế
            context.writer.text(value)
        }
    }
}


/**
 * Text renderer for parameter nodes
 */
class ParameterTextNodeRenderer(
    private val context: TextContentNodeRendererContext,
    private val parameters: Map<String, String>
) : NodeRenderer {
    override fun getNodeTypes(): Set<Class<out Node>> = setOf(ParameterNode::class.java)

    override fun render(node: Node) {
        if (node is ParameterNode) {
            val paramName = node.name
            val value = parameters[paramName] ?: "{{$paramName}}"
            context.writer.write(value)
        }
    }
}

/**
 * HTML renderer extension for parameters
 */
class ParameterHtmlRendererExtension(private val parameters: Map<String, String>) :
    HtmlRenderer.HtmlRendererExtension {
    override fun extend(rendererBuilder: HtmlRenderer.Builder) {
        rendererBuilder.nodeRendererFactory { context ->
            ParameterHtmlNodeRenderer(context, parameters)
        }
    }
}

/**
 * Text renderer extension for parameters
 */
class ParameterTextRendererExtension(private val parameters: Map<String, String>) :
    TextContentRenderer.TextContentRendererExtension {
    override fun extend(rendererBuilder: TextContentRenderer.Builder) {
        rendererBuilder.nodeRendererFactory { context ->
            ParameterTextNodeRenderer(context, parameters)
        }
    }
}

/**
 * Utility class for parsing and rendering markdown with parameters
 */
object MarkdownWithParametersParser {
    /**
     * Parse markdown text and replace parameters with values from the provided map
     * @param markdown The markdown text with parameters in the format {{paramName}}
     * @param parameters Map of parameter names to values
     * @return Rendered HTML string
     */
    fun parseToHtml(markdown: String, parameters: Map<String, String>): String {
        val extensions = listOf<Extension>(
//            ParameterInlineParserExtension(),
            TablesExtension.create(),
            AutolinkExtension.create(),
            FootnotesExtension.create(),
            HeadingAnchorExtension.create(),
            InsExtension.create(),
            StrikethroughExtension.create(),
            TaskListItemsExtension.create(),
        )

        val parser = Parser.builder()
            .extensions(extensions)
            .build()

        val parsedMarkdown = replaceParameters(markdown, parameters)
        val document = parser.parse(parsedMarkdown)

        val renderer = HtmlRenderer.builder()
            .extensions(
                listOf(
//                    ParameterHtmlRendererExtension(parameters),
                    TablesExtension.create(),
                    AutolinkExtension.create(),
                    FootnotesExtension.create(),
                    HeadingAnchorExtension.create(),
                    InsExtension.create(),
                    StrikethroughExtension.create(),
                    TaskListItemsExtension.create(),
                )
            )
            .build()

        return renderer.render(document)
    }

    /**
     * Parse markdown text and replace parameters with values from the provided map
     * @param markdown The markdown text with parameters in the format {{paramName}}
     * @param parameters Map of parameter names to values
     * @return Plain text string
     */
    fun parseToText(markdown: String, parameters: Map<String, String>): String {
        val extensions = listOf<Extension>(ParameterInlineParserExtension())

        val parser = Parser.builder()
            .extensions(extensions)
            .build()

        val document = parser.parse(markdown)

        val renderer = TextContentRenderer.builder()
            .extensions(listOf(ParameterTextRendererExtension(parameters)))
            .build()

        return renderer.render(document)
    }
}

/**
 * Jetpack Compose component for displaying markdown with parameters
 */
@Composable
fun MarkdownWithParams(
    markdown: String,
    params: Map<String, String>,
    modifier: Modifier = Modifier
) {
    val htmlContent = MarkdownWithParametersParser.parseToHtml(markdown, params)

    AndroidView(
        factory = { ctx ->
            TextView(ctx).apply {
                // Set some default styling if needed
                textSize = 16f
            }
        },
        modifier = modifier,
        update = { textView ->
            textView.text = HtmlCompat.fromHtml(
                htmlContent,
                HtmlCompat.FROM_HTML_MODE_COMPACT
            )
        }
    )
}

fun replaceParameters(markdown: String, parameters: Map<String, String>): String {
    val regex = Regex("""\{\{(.*?)\}\}""")
    return regex.replace(markdown) { matchResult ->
        val key = matchResult.groupValues[1].trim()
        parameters[key] ?: matchResult.value  // fallback nếu không có key
    }
}

/**
 * Example usage preview*/
@Preview(showBackground = true)
@Composable
fun MarkdownWithParamsPreview() {

    val simpleMarkdown = """
    Hello **{{name}}**!

    Welcome to *{{appName}}*. Enjoy your stay.
""".trimIndent()

// 2. Map chứa giá trị thay thế
    val simpleParams = mapOf(
        "name" to "Alice",
        "appName" to "MyCoolApp"
    )


    KaniTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                MarkdownWithParams(
                    markdown = simpleMarkdown,
                    params = simpleParams,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Plain Text Example:",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                Text(
                    text = MarkdownWithParametersParser.parseToText(simpleMarkdown, simpleParams),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
