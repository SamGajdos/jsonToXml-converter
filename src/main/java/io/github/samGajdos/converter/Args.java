package io.github.samGajdos.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.IParametersValidator;
import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;


/**
 * JSON to XML Converter
 * CLI Arguments parser
 * Author: Samuel Gajdos with a help of docs, forums and LLMs
 * Date: June 2026
 */
public class Args {
    @Parameter
    private List<String> parameters = new ArrayList<>();

    @Parameter(names = { "--vstup", "-i"}, required = true,
        description = "Cesta k adresáru v ktorom sa nachádzajú súbory v JSON formáte.",
        validateWith = PathDir.class)
    private String vstup;

    @Parameter(names = {"--vystup", "-o"}, required = true,
        description = "Cesta k výstupnému adresáru.",
        validateWith = PathDir.class)
    private String vystup;

    @Parameter(names = "--platnostOd", required = true,
        description = "dátum platnosti od vo formáte YYYY-MM-DD",
        converter = DateConverter.class)
    private LocalDate platnostOd;

    @Parameter(names = "--platnostDo", required = true,
        description = "dátum platnosti do vo formáte YYYY-MM-DD",
        converter = DateConverter.class)
    private LocalDate platnostDo;

    @Parameter(names = "--help", help = true)
    private boolean help;

    public String getVstup() {
        return vstup;
    }

    public String getVystup() {
        return vystup;
    }

    public LocalDate getPlatnostOd() {
        return platnostOd;
    }

    public LocalDate getPlatnostDo() {
        return platnostDo;
    }

    public static class PathDir implements IParameterValidator {
        public void validate(String name, String value) throws ParameterException {
            Path directory = Paths.get(value);
            if (!directory.toFile().isDirectory()) {
                throw new ParameterException("Parameter " + name + " should be a directory");
            }
        }
    }

    public static class DateConverter implements IStringConverter<LocalDate> {
        @Override
        public LocalDate convert(String value) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            try {
                return LocalDate.parse(value, formatter);
            } catch (DateTimeParseException e) {
                throw new ParameterException("Value: " + value + " should be a valid date in format YYYY-MM-DD");
            }
        }
    }

    public String validateDateInterval() {
        if (platnostOd.isAfter(platnostDo)) {
            return "Value of the platnostOd can't be after the platnostDo\n";
        } else {
          return "";
        }
    }
}
