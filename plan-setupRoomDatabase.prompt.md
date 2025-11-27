## Plan: Setup Room Database in Android Compose Project

Implement a complete Room Database setup with KSP processing, sample Task entity, repository pattern, ViewModel integration, and Compose UI displaying persisted data from the local database.

### Steps

1. **Configure dependencies and KSP plugin** — Add Room version (2.6.1), KSP plugin (2.0.0-1.0.21), and lifecycle-viewmodel-compose to [libs.versions.toml](D:\tests\RoomTester\gradle\libs.versions.toml), apply KSP plugin in [build.gradle.kts](D:\tests\RoomTester\app\build.gradle.kts), and configure KSP schema export directory

2. **Create database layer** — Define `Task` entity class with `@Entity`, `@PrimaryKey`, and `@ColumnInfo` annotations; create `TaskDao` interface with `@Insert`, `@Query`, `@Update`, and `@Delete` methods returning Flow for reactive updates; build `TaskDatabase` abstract class extending `RoomDatabase` with singleton pattern

3. **Implement repository pattern** — Create `TaskRepository` class wrapping `TaskDao` operations and exposing Flow-based data streams for the UI layer to observe database changes

4. **Build ViewModel layer** — Create `TaskViewModel` extending `ViewModel` with StateFlow exposing task list, handling database operations via repository, using `viewModelScope` for coroutines, and managing UI state

5. **Design Compose UI** — Update [MainActivity.kt](D:\tests\RoomTester\app\src\main\java\com\test\roomtester\MainActivity.kt) with lazy column displaying tasks, floating action button for adding tasks, swipe-to-delete functionality, and collectAsState for observing ViewModel StateFlow

6. **Add dependency injection setup** — Create application class initializing database instance, implement manual DI container or use simple companion object pattern for providing database and repository instances to ViewModel

### Further Considerations

1. **Testing approach** — Use in-memory database for instrumented tests in [ExampleInstrumentedTest.kt](D:\tests\RoomTester\app\src\androidTest\java\com\test\roomtester\ExampleInstrumentedTest.kt), test DAO operations, and verify Flow emissions; unit test repository with fake DAO implementation

2. **Migration strategy** — Should migrations be included from the start, or add them when schema changes? Consider implementing `fallbackToDestructiveMigration()` for development vs proper migration strategy for production

3. **Coroutine scope** — Use `viewModelScope` for ViewModel operations; should repository operations use `Dispatchers.IO` explicitly or rely on Room's default dispatcher?

