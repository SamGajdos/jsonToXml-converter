package io.github.samGajdos.converter;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.IParameterValidator;
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

    @Parameters(paremetersValidators = platnostOdPlatnostDoMutualExl.class)   
    class paramDates {
        @Parameter(names = "--platnostOd", required = true,
            description = "dátum platnosti od vo formáte YYYY-MM-DD",
            validateWith = IODate.class)
        private String platnostOd;

        @Parameter(names = "--platnostDo", required = true,
            description = "dátum platnosti do vo formáte YYYY-MM-DD",
            validateWith = IODate.class)
        private String platnostDo;
    }

    public static class PathDir implements IParameterValidator {
        public void validate(String name, String value) throws ParameterException {
            Path directory = Paths.get(value);
            if (!directory.toFile().isDirectory()) {
                throw new ParameterException("Parameter " + name + " should be a directory");
            }
        }
    }

    public static class IODate implements IParameterValidator {
        public void validate(String name, String value) throws ParameterException {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            try {
                LocalDate.parse(value, formatter);
            } catch (DateTimeParseException e) {
                throw new ParameterException("Parameter " + name + " should be a valid date in format YYYY-MM-DD");
            }  
        }
    }

    public static class platnostOdPlatnostDoMutualExl implements IParameterValidator {
        @Override
        public void validate(Map<String, Object> parameters) throws ParameterException {  
            LocalDate pOd = LocalDate.parse(parameters.get("--platnostOd"));
            LocalDate pDo = LocalDate.parse(parameters.get("--platnostDo"));
            
            if (pDo.isBefore(pOd)) {
                throw new ParameterException("Value of platnostOd must be before the platnostDo");
            }
        } 
    }
}
