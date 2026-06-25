package io.github.samGajdos.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.GregorianCalendar;
import java.time.LocalDate;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.DatatypeConfigurationException;
import generated.MessageType;

public class MessageMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageMapper.class);

    public MessageType map(MessageDto dto) {

        MessageType xsd = new MessageType();

        xsd.setId(dto.getId());
        xsd.setType(dto.getType());

        LocalDate createdLDate = LocalDate.parse(dto.getCreated());
        try {
            xsd.setCreated(DatatypeFactory.newInstance().newXMLGregorianCalendar(createdLDate.toString()));
        }
        catch(DatatypeConfigurationException e) {
            LOGGER.error("Couldn't parse created from String to XMLGregorianCalendar");
        }

        xsd.setAmount(dto.getAmount());
        xsd.setVat(dto.getVat());
        xsd.setAmountWithVat(dto.getAmountWithVat());

        return xsd;
    }
}