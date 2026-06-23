package io.github.samGajdos.converter;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.Set;
import java.util.List;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import generated.Messages;
import generated.MessageType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.modelmapper.ModelMapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;


/**
 * JSON to XML Converter
 * Author: Samuel Gajdos
 * Date: June 2026
 */
public class App {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    private static final LocalDate from = LocalDate.of(2025, 1, 1);
    private static final LocalDate to   = LocalDate.of(2026, 12, 31);

    public static void main(String[] args) throws Exception {
        LOGGER.info("Hello from Logger!");

        String testJson = """
        [
          { "id": "MSG001", "type": "ORDER",   "created": "2025-01-05", "amount": 120.50, "vat": 20 },
          { "id": "MSG002", "type": "INVOICE", "created": "2025-12-20", "amount": 89.00,  "vat": 20 },
          { "id": "MSG003", "type": "ORDER",   "created": "", "amount": 10.00,  "vat": 10 },
          { "id": "",       "type": "ORDER",   "created": "2026-02-01", "amount": 15.10,  "vat": 200}
        ]
        """;

        ObjectMapper objectMapper = new ObjectMapper();
        List<MessageDto> messages = objectMapper.readValue(testJson, new TypeReference<List<MessageDto>>(){}); 

        // MessageDto[] messages = objectMapper.readValue(testJson,MessageDto[].class);

        // List<MessageType> messageTypeList = objectMapper.readValue(testJson, new TypeReference<List<MessageType>>(){});
        // Messages messagesXml = new Messages();
        // messagesXml.getMessage().addAll(messageTypeList);
           
        Messages messagesXml = new Messages();

        for (MessageDto message : messages) {
            
            String errors = validate(message);
            if (errors != "") {
               logError(message, errors);
               continue; 
            }

            // TODO add try catch block here?
            message.setAmountWithVat(calculateAmountWithVat(message));
            
            //Conversion part
            MessageMapper messageMapper = new MessageMapper();
            MessageType messageXSD = messageMapper.map(message);
            messagesXml.getMessage().add(messageXSD);
        }

        // TODO: add jaxbexception
        JAXBContext context = JAXBContext.newInstance(Messages.class);
        Marshaller mar = context.createMarshaller();
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        mar.marshal(messagesXml, new File("./basic.xml"));
    }

    public static String validate(MessageDto message) {
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

                if (createdDate.isBefore(from) || createdDate.isAfter(to)) {
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

    public static void logError(MessageDto message, String errString) {
        LOGGER.error("Found incorrect values in this JSON item: \n" + message.toString());
        errString.lines().forEach(LOGGER::error);
        LOGGER.error("Skipping this message entry\n\n");
    }
}
