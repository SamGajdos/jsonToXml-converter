package io.github.samGajdos.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Set;
import java.util.List;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.File;
import java.io.IOException;
import generated.Messages;
import generated.MessageType;
import com.beust.jcommander.ParameterException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.beust.jcommander.JCommander;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import org.apache.commons.io.FilenameUtils;


/**
 * JSON to XML Converter
 * Main App class
 * Author: Samuel Gajdos with a help of docs, forums and LLMs
 * Date: June 2026
 */
public class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(final String[] argv) throws Exception {
        
        Args args = processArgs(argv);
        String inputDir = args.getVstup();
        String outputDir = args.getVystup();
        LocalDate validFrom = args.getPlatnostOd();
        LocalDate validTo = args.getPlatnostDo();

        List<Path> jsonPaths = new ArrayList<Path>();
        try {
            jsonPaths = jsonFilePaths(inputDir);
        } catch(Exception e){
            logErrorWithExit(e.getMessage());
        }

        // Iterate through files
        for(Path jPath : jsonPaths) {            
            // Read json items - Messages
            ObjectMapper objectMapper = new ObjectMapper();
            // TODO ad try catch for jsonParseException
            List<MessageDto> messages;
            try {
                messages = objectMapper.readValue(jPath.toFile(), new TypeReference<List<MessageDto>>(){});            
            } catch(Exception e) {
                LOGGER.error("Couldn't read JSON file: " + jPath.toString() +
                             " " + e.getMessage() + "\nSkipping.\n");
                continue;
            }
            Messages messagesXml = new Messages();

            for (MessageDto message : messages) {  
                String errors = validate(message, validFrom, validTo);
                if (!errors.isEmpty()) {
                   logErrorMsg(message, errors);
                   continue; 
                }

                message.setAmountWithVat(calculateAmountWithVat(message));
                
                //Map to xsd schema: MessageDto -> MessageType
                MessageMapper messageMapper = new MessageMapper();
                MessageType messageXSD = messageMapper.map(message);
                messagesXml.getMessage().add(messageXSD);
            }

            // TODO: add jaxbexception
            
            JAXBContext context = JAXBContext.newInstance(Messages.class);
            Marshaller mar = context.createMarshaller();
            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            String jsonBaseName = jPath.getFileName().toString();
            String jsonBaseWIthoutExt = FilenameUtils.removeExtension(jsonBaseName);
            Path xmlPath = Paths.get(outputDir, jsonBaseWIthoutExt + ".xml");
            mar.marshal(messagesXml, new File(xmlPath.toString()));
        }
    }

    // Source: Stack Overflow and Chat GPT
    public static List<Path> jsonFilePaths(String dir) throws IOException {
        try (Stream<Path> stream = Files.walk(Paths.get(dir), 1)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".json"))
                    .toList();
        }
    }

    public static String validate(MessageDto message, LocalDate validFrom, LocalDate validTo) {
            String errors = "";
            
            // Beans validation
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<MessageDto>> violations = validator.validate(message);
            if (violations != null) {
                for (ConstraintViolation<MessageDto> violation : violations) {
                    errors += violation.getMessage(); 
                }
            }

            // Custom validation of a 'created' attribute 
            LocalDate createdDate;
            try {
                createdDate = LocalDate.parse(message.getCreated());

                if (createdDate.isBefore(validFrom) || createdDate.isAfter(validTo)) {
                    errors += "attribute 'created': Outside of specified range\n";
                }
            }
            catch(Exception e) {
                errors += "attribute 'created': Empty value or wrong date format\n";
            }

            return errors;
    }

    public static BigDecimal calculateAmountWithVat(MessageDto message) {
        // 1 + vat/100
        BigDecimal formulaCoefficient = BigDecimal.valueOf(message.getVat())
                                        .divide(BigDecimal.valueOf(100))
                                        .add(BigDecimal.valueOf(1));
        // amount * (1 + vat/100)
        BigDecimal amountWithVat = formulaCoefficient.multiply(message.getAmount()); 
        amountWithVat = amountWithVat.setScale(2, RoundingMode.CEILING);
        return amountWithVat;
    }

    public static void logErrorMsg(MessageDto message, String errString) {
        LOGGER.error("Found incorrect values in this JSON item: \n" + message.toString());
        errString.lines().forEach(LOGGER::error);
        LOGGER.error("Skipping this message entry in this processing\n\n");
    }

    public static Args processArgs(String[] argv) {
        for (String arg : argv) {
            LOGGER.debug(arg);
        }

        Args args = new Args();
        JCommander jc = JCommander.newBuilder().addObject(args).build();
        try {
            jc.parse(argv);
        } catch (ParameterException e) {
            logErrorWithExit(e.getMessage());
            jc.usage();
        }

        String errMsg = args.validateDateInterval();
        if (!errMsg.isEmpty()) {
            logErrorWithExit(errMsg);
        }

        return args;
    }

    public static void logErrorWithExit(String errString) {
        errString.lines().forEach(LOGGER::error);
        LOGGER.error("Exiting the app with return code 1");
        System.exit(1);
    }
}
