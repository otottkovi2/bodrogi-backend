package hu.almokatepitunk.backend.models

import org.springframework.data.mongodb.core.mapping.Document
import java.net.URI

@Document("images")
data class Image(val url: URI)
