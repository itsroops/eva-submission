package embl.ebi.variation.eva;

import embl.ebi.variation.eva.seqrep_fasta_dl.ENASequenceReportDownload;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@SpringBootApplication
@IntegrationComponentScan
public class EvaIntegrationApplication {


	public static void main(String[] args) {

        ConfigurableApplicationContext ctx = SpringApplication.run(EvaIntegrationApplication.class, args);

		Properties properties = loadProperties();

        String assemblyAccession = "GCA_000001405.10";
        String localAssemblyDirectoryRoot = "/home/tom/Job_Working_Directory/Java/eva-integration/src/main/resources/test_dl/ftpInbound";
        String sequenceReportFile = Paths.get(localAssemblyDirectoryRoot, assemblyAccession + "_sequence_report_head5.txt").toString();

        String fastaFile = "/home/tom/Job_Working_Directory/Java/eva-integration/src/main/resources/test_dl/ftpInbound/GCA_000001405.10.fasta2";

//        setupEnvironment(ctx, assemblyAccession);

        Map<String, Object> headers = new HashMap<>();
        headers.put("seqReportLocalPath", sequenceReportFile);
        headers.put("enaFtpSeqRepDir", "pub/databases/ena/assembly/");
        headers.put("fastaLocal", fastaFile);
        GenericMessage message = new GenericMessage<String>(sequenceReportFile, headers);

        if (!new File(sequenceReportFile).exists()){
            MessageChannel inputChannel = ctx.getBean("inputChannel", MessageChannel.class);
            inputChannel.send(message);
        } else if (!new File(fastaFile).exists()){
            MessageChannel channelIntoDownloadFasta = ctx.getBean("channelIntoDownloadFasta", MessageChannel.class);
            channelIntoDownloadFasta.send(message);
        }

//        ctx.close();
	}


	private static Properties loadProperties(){
		Properties prop = new Properties();
		try {
			prop.load(ENASequenceReportDownload.class.getClassLoader().getResourceAsStream("application.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;
	}
}
