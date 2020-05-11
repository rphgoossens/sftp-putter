package nl.terrax.camel.main;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.xfer.InMemorySourceFile;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Main {

    public static void main(String[] args) throws IOException {
        Main main = new Main();
        main.drive();
    }

    // Read file in inputstream
    private void drive() throws IOException {
        final File initialFile = new File("src/main/resources/sample.txt");
        final long fileSize = FileUtils.sizeOf(initialFile);
        final String fileName = "sample-copy.txt";

        try (InputStream targetStream = FileUtils.openInputStream(initialFile)) {
            // create MemoryFile
            InMemorySourceFile sourceFile = makeInmemorySourceFile(targetStream, fileName, fileSize);
            doWithSFTPClient(client -> client.put(sourceFile, "/sftpuser"));
        }
    }

    private void doWithSFTPClient(ConsumerWithIOException<SFTPClient> sftpOperation) throws IOException {
        try (SSHClient sshClient = new SSHClient()) {
            sshClient.addHostKeyVerifier(new PromiscuousVerifier());
            sshClient.connect("localhost");
            sshClient.authPublickey("sftpuser", "/home/rphgoossens/.ssh/id_rsa");

            // This is behavior!!!!!!!!!!!!
            try (SFTPClient sftpClient = sshClient.newSFTPClient()) {
                sftpOperation.accept(sftpClient);

            }
        }
    }


    private InMemorySourceFile makeInmemorySourceFile(final InputStream targetStream, final String fileName, final long fileSize) {

        return new InMemorySourceFile() {
            public String getName() {
                return fileName;
            }

            public long getLength() {
                return fileSize;
            }

            public InputStream getInputStream() {
                return targetStream;
            }
        };


    }

}
