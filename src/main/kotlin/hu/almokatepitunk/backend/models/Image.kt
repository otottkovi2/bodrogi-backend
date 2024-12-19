package hu.almokatepitunk.backend.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.net.URI

@Document("images")
data class Image(@Id val id: String, val url: URI){
    constructor(id:String, url: String) : this(id,URI.create(url))
}
