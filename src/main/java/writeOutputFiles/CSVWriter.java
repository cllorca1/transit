package writeOutputFiles;

import transitSystem.TransitDataContainer;

public interface CSVWriter {

    void write (String fileName, TransitDataContainer transitDataContainer);
}
