package delta.cion.cherry.api.plugin_new.loader.library.impl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.supplier.RepositorySystemSupplier;
import org.eclipse.aether.supplier.SessionBuilderSupplier;
import org.eclipse.aether.transfer.AbstractTransferListener;
import org.eclipse.aether.transfer.TransferCancelledException;
import org.eclipse.aether.transfer.TransferEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.endelith.plugin.loader.library.ClasspathLibrary;
import xyz.endelith.plugin.loader.library.LibraryLoadingException;
import xyz.endelith.plugin.loader.library.LibraryStore;

public final class MavenLibraryResolver implements ClasspathLibrary {

    private static final Logger LOGGER = LoggerFactory.getLogger(MavenLibraryResolver.class);

    public static final String MAVEN_CENTRAL_DEFAULT_MIRROR = defaultMavenCentralMirror();

    private static final List<String> MAVEN_CENTRAL_URLS = List.of(
            "https://repo1.maven.org/maven2",
            "http://repo1.maven.org/maven2",
            "https://repo.maven.apache.org/maven2",
            "http://repo.maven.apache.org/maven2"
    );

    private final RepositorySystem repository;
    private final RepositorySystemSession session;

    private final List<Dependency> dependencies = new ArrayList<>();
    private final List<RemoteRepository> repositories = new ArrayList<>();

    public MavenLibraryResolver() {
        this.repository = new RepositorySystemSupplier().get();

        AbstractTransferListener transferListener = new AbstractTransferListener() {
            @Override
            public void transferInitiated(TransferEvent event) throws TransferCancelledException {
                LOGGER.info(
                        "Downloading {}",
                        event.getResource().getRepositoryUrl() + event.getResource().getResourceName()
                );
            }
        };

        RepositorySystemSession.SessionBuilder sessionBuilder = new SessionBuilderSupplier(this.repository).get();
        sessionBuilder.setSystemProperties(System.getProperties());
        sessionBuilder.setChecksumPolicy(RepositoryPolicy.CHECKSUM_POLICY_FAIL);
        sessionBuilder.setTransferListener(transferListener);

        LocalRepository localRepository = new LocalRepository(Path.of("libraries"));
        sessionBuilder.withLocalRepositories(localRepository);

        this.session = sessionBuilder.build();
    }

    public void addDependency(Dependency dependency) {
        this.dependencies.add(dependency);
    }

    public void addRepository(RemoteRepository remoteRepository) {
        if (MAVEN_CENTRAL_URLS.stream().anyMatch(remoteRepository.getUrl()::startsWith)) {
            LOGGER.warn(
                    "Use of Maven Central as a CDN is against the Maven Central Terms of Service. "
                            + "Use MavenLibraryResolver.MAVEN_CENTRAL_DEFAULT_MIRROR instead.",
                    new RuntimeException(String.format(
                            "Plugin used Maven Central for library resolution (%s)",
                            remoteRepository
                    ))
            );
        }

        this.repositories.add(remoteRepository);
    }

    @Override
    public void register(LibraryStore store) throws LibraryLoadingException {
        List<RemoteRepository> repos = this.repository.newResolutionRepositories(this.session, this.repositories);

        DependencyResult result;
        try {
            result = this.repository.resolveDependencies(
                    this.session,
                    new DependencyRequest(
                            new CollectRequest(
                                    (Dependency) null,
                                    this.dependencies,
                                    repos
                            ),
                            null
                    )
            );
        } catch (DependencyResolutionException ex) {
            throw new LibraryLoadingException("Error resolving libraries", ex);
        }

        for (ArtifactResult artifact : result.getArtifactResults()) {
            store.addLibrary(artifact.getArtifact().getPath());
        }
    }

    private static String defaultMavenCentralMirror() {
        String central = System.getenv("DEFAULT_CENTRAL_REPOSITORY");
        if (central == null) {
            central = "https://maven-central.storage-download.googleapis.com/maven2";
        }

        return central;
    }
}
