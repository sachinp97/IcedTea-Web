package net.adoptopenjdk.icedteaweb.classloader;

import net.adoptopenjdk.icedteaweb.classloader.JnlpApplicationClassLoader.LoadableJar;
import net.adoptopenjdk.icedteaweb.logging.Logger;
import net.adoptopenjdk.icedteaweb.logging.LoggerFactory;
import net.sourceforge.jnlp.JNLPFile;
import net.sourceforge.jnlp.LaunchException;
import net.sourceforge.jnlp.runtime.ApplicationPermissions;
import net.sourceforge.jnlp.runtime.SecurityDelegate;
import net.sourceforge.jnlp.runtime.SecurityDelegateNew;
import net.sourceforge.jnlp.signing.NewJarCertVerifier;
import net.sourceforge.jnlp.signing.SignVerifyUtils;
import net.sourceforge.jnlp.tools.CertInformation;

import java.io.File;
import java.net.URISyntaxException;
import java.security.cert.CertPath;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Responsible for validating the trust we have in the application.
 * <ul>
 *     <li>Jar Signatures</li>
 *     <li>JNLP Signature</li>
 *     <li>Certificates used for signing</li>
 *     <li>Content of manifest</li>
 * </ul>
 */
public class ApplicationTrustValidator {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationTrustValidator.class);

    private final SecurityDelegate securityDelegate;

    private final NewJarCertVerifier certVerifier;


    public ApplicationTrustValidator(final JNLPFile file, final ApplicationPermissions applicationPermissions) {
        this.certVerifier = new NewJarCertVerifier();
        this.securityDelegate = new SecurityDelegateNew(applicationPermissions, file, certVerifier);

    }

    protected void validateJars(List<LoadableJar> jars) {
        if (securityDelegate.getRunInSandbox()) {
            return;
        }

        try {
            certVerifier.addAll(toFiles(jars));

            final Set<CertPath> fullySigningCertificates = certVerifier.getFullySigningCertificates();

            if (fullySigningCertificates.isEmpty()) {
                // TODO: HILFE
            } else {
                final ZonedDateTime now = ZonedDateTime.now();
                final Map<CertPath, CertInformation> certInfos = fullySigningCertificates.stream()
                        .collect(Collectors.toMap(Function.identity(), certPath -> SignVerifyUtils.calculateCertInformationFor(certPath, now)));

                final boolean hasTrustedFullySigningCertificate = certInfos.values().stream()
                        .filter(infos -> infos.isRootInCacerts() || infos.isPublisherAlreadyTrusted())
                        .anyMatch(infos -> !infos.hasSigningIssues());

                if (hasTrustedFullySigningCertificate) {
                    return;
                }

                // find certPath with best info - what is best info? - no issues, trusted RootCA

            }

            // TODO: work in progress
            if (!certVerifier.isFullySigned()) {
                securityDelegate.promptUserOnPartialSigning();
            }
//                if (!certVerifier.isFullySigned() && !certVerifier.getAlreadyTrustPublisher()) {
//                    certVerifier.checkTrustWithUser(securityDelegate, file);
//                }
        } catch (LaunchException e) {
            // TODO: LaunchException should not be wrapped in a RuntimeException
            throw new RuntimeException(e);
        }
    }

    private static List<File> toFiles(List<LoadableJar> jars) {
        return jars.stream()
                .map(loadableJar -> {
                    try {
                        return new File(loadableJar.getLocation().toURI());
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }
}
