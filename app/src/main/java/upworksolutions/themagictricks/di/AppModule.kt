package upworksolutions.themagictricks.di

import upworksolutions.themagictricks.repository.TricksRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideTricksRepository(firestore: FirebaseFirestore): TricksRepository {
        return TricksRepository(firestore)
    }
} 