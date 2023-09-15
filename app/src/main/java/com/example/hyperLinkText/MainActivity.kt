package com.example.hyperLinkText

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.example.hyperLinkText.ui.theme.hyperLinkTextTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            hyperLinkTextTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var text = "https://www.youtube.com"
                    TextSelectionExample(applicationContext,
                        "Here's a link to a website: https://www.youtube.com. Check it out!")
                }
            }
        }
    }
}
@Composable
fun TextSelectionExample(context: Context, paragraph: String) {
    var linkForSite by remember {
        mutableStateOf(
            "")
    }

    val annotatedString = buildAnnotatedString {
        val pattern = Regex("""https://.*?\.com""")
        val matches = pattern.findAll(paragraph)

        var lastIndex = 0

        for (match in matches) {
            val startIndex = match.range.first
            val endIndex = match.range.last + 1
            append(paragraph.substring(lastIndex, startIndex))
            val linkText = paragraph.substring(startIndex, endIndex)
            linkForSite = linkText
            pushStringAnnotation("LINK", linkText)
            withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline, color = Color.Blue)) {
                append(linkText)
            }
            pop()

            lastIndex = endIndex
        }
        if (lastIndex < paragraph.length) {
            append(paragraph.substring(lastIndex))
        }
    }

    SelectionContainer {
        Text(
            text = annotatedString,
            modifier = Modifier
                .padding(16.dp)
                .clickable {
                    val uri = linkForSite
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(context, intent, null)
                },
            style = TextStyle(fontSize = 18.sp)

        )
    }
}

@Composable
fun HyperlinkExample(textInput: String, context: Context) {
    val text = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = Color.Blue,
                textDecoration = TextDecoration.Underline,
                fontSize = 16.sp
            )
        ) {
            val linkUri = if(textInput.contains("https://") || textInput.contains("http://")) {
                textInput
            } else {
                ""
            }
            addStringAnnotation(
                tag = "URL",
                annotation = linkUri,
                start = 0,
                end = textInput.length
            )
            append(textInput)
        }
    }

    ClickableText(
        text = text,
        modifier = Modifier.padding(16.dp),
        onClick = { offset ->
            if(text.contains("https://") || text.contains("http://")) {
                text.getStringAnnotations("URL", offset, offset).firstOrNull()?.let { annotation ->
                    val uri = annotation.item
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(context, intent, null)
                }
            } else {
                Unit
            }
        }
    )
}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    hyperLinkTextTheme {
        Greeting("Android")
    }
}