package hu.almokatepitunk.backend.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.net.URI

@Document("images")
data class Image(val url: URI){
    constructor(url: String) : this(URI.create(url))
    constructor(id:String, url: String) : this(URI.create(url)) {
        this.id = id
    }
    @Id
    lateinit var id: String
}
