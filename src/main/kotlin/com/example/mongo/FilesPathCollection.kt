package com.example.mongo

import com.example.mongo.SystemSettingsDataBase.NameCollections.paths
import com.example.routes.fileLoad.models.TypeLoadFile
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo

val uriMongo =
    "mongodb://testPetApp:563214789Qq@192.168.0.53:27017/?authSource=admin&replicaSet=rs1&directConnection=true"

val KClient = KMongo.createClient(uriMongo)
    .coroutine

open class MongoCollection<T>(
    val db: CoroutineDatabase,
    val name: String
)

inline fun <reified T : Any> MongoCollection<T>.getCollection(advName: String = ""): CoroutineCollection<T> {
    return db.getCollection(this.name + advName)
}

@kotlinx.serialization.Serializable
data class FilesPath(
    @BsonId
    val type: String,
    val path: String
)

object SystemSettingsDataBase {

    val db = KClient.getDatabase("system_settings")

    object NameCollections {
        const val paths = "files_paths"
    }

    object FilesPaths: MongoCollection<FilesPath>(db, paths)

}

suspend fun SystemSettingsDataBase.FilesPaths.getPath(type: String): String? {
    return getCollection().findOne(FilesPath::type eq type)?.path
}