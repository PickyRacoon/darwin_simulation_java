package agh;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CSVLogger implements AutoCloseable{
    private PrintWriter writer;
    private boolean headerWritten =  false;

    public CSVLogger(File file) throws IOException {
        // czy na pewno wszytko istnieje
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        this.writer = new PrintWriter(new FileWriter(file, true));
    }

    public void logDay(int day, int animalsCount, int grassCount, int emptySquares,
                       double avgEnergy, double avgLifeSpan, double avgChildrenCount, String mostPopular) {

        if (!headerWritten) {
            writer.println("Day;Animals;Grasses;EmptySquares;AvgEnergy;AvgLifeSpan;AvgChildren;MostPopularGenotype");
            headerWritten = true;
        }

        writer.printf("%d;%d;%d;%d;%.2f;%.2f;%.2f;%s%n",
                day, animalsCount, grassCount, emptySquares, avgEnergy, avgLifeSpan, avgChildrenCount, mostPopular);

        writer.flush();
    }

    @Override
    public void close() {
        if (writer != null) {
            writer.close();
        }
    }
}
