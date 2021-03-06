package com.quorum.tessera.config.cli.parsers;

import com.quorum.tessera.config.ArgonOptions;
import com.quorum.tessera.config.AzureKeyVaultConfig;
import com.quorum.tessera.config.KeyVaultConfig;
import com.quorum.tessera.config.KeyVaultType;
import com.quorum.tessera.config.cli.CliException;
import com.quorum.tessera.config.keypairs.ConfigKeyPair;
import com.quorum.tessera.config.util.JaxbUtil;
import com.quorum.tessera.key.generation.KeyGenerator;
import com.quorum.tessera.key.generation.KeyGeneratorFactory;
import org.apache.commons.cli.CommandLine;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;

public class KeyGenerationParser implements Parser<List<ConfigKeyPair>> {

    private final KeyGeneratorFactory factory = KeyGeneratorFactory.newFactory();

    private final Validator validator = Validation.byDefaultProvider()
        .configure()
        .ignoreXmlConfiguration()
        .buildValidatorFactory()
        .getValidator();

    public List<ConfigKeyPair> parse(final CommandLine commandLine) throws IOException {

        final ArgonOptions argonOptions = this.argonOptions(commandLine).orElse(null);
        final KeyVaultConfig keyVaultConfig = this.keyVaultConfig(commandLine).orElse(null);

        final KeyGenerator generator = factory.create(keyVaultConfig);

        if (commandLine.hasOption("keygen")) {
            return this.filenames(commandLine)
                .stream()
                .map(name -> generator.generate(name, argonOptions))
                .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    private Optional<ArgonOptions> argonOptions(final CommandLine commandLine) throws IOException {

        if (commandLine.hasOption("keygenconfig")) {
            final String pathName = commandLine.getOptionValue("keygenconfig");
            final InputStream configStream = Files.newInputStream(Paths.get(pathName));

            final ArgonOptions argonOptions = JaxbUtil.unmarshal(configStream, ArgonOptions.class);
            return Optional.of(argonOptions);
        }

        return Optional.empty();
    }

    private List<String> filenames(final CommandLine commandLine) {

        if (commandLine.hasOption("filename")) {

            final String keyNames = commandLine.getOptionValue("filename");
            if (keyNames != null) {
                return Stream.of(keyNames.split(",")).collect(Collectors.toList());
            }

        }

        return singletonList("");

    }

    private Optional<KeyVaultConfig> keyVaultConfig(CommandLine commandLine) {
        if(!commandLine.hasOption("keygenvaulttype") && !commandLine.hasOption("keygenvaulturl")) {
            return Optional.empty();
        }

        String t = commandLine.getOptionValue("keygenvaulttype");

        try {
            KeyVaultType.valueOf(t);
        } catch(IllegalArgumentException | NullPointerException e) {
            throw new CliException("Key vault type either not provided or not recognised.  Ensure provided value is UPPERCASE and has no leading or trailing whitespace characters");
        }

        String keyVaultUrl = commandLine.getOptionValue("keygenvaulturl");

        //Only Azure supported atm so no need to check keyvaulttype
        KeyVaultConfig keyVaultConfig = new AzureKeyVaultConfig(keyVaultUrl);

        Set<ConstraintViolation<AzureKeyVaultConfig>> violations = validator.validate((AzureKeyVaultConfig)keyVaultConfig);

        if(!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        return Optional.of(keyVaultConfig);
    }

}
