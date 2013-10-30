package gr.ekt.bteio.loaders;

import gr.ekt.bte.core.DataLoader;
import gr.ekt.bte.core.DataLoadingSpec;
import gr.ekt.bte.core.RecordSet;
import gr.ekt.bte.exceptions.MalformedSourceException;

import java.util.ArrayList;
import java.util.List;

public class MultiSourceDataLoader implements DataLoader {
    private List<DataLoader> dataLoaders;

    public MultiSourceDataLoader() {
        dataLoaders = new ArrayList<DataLoader>();
    }

    @Override
    public RecordSet getRecords() throws MalformedSourceException {
        RecordSet ret = new RecordSet();

        for (DataLoader loader : dataLoaders) {
            RecordSet cSet = loader.getRecords();
            ret.addAll(cSet);
        }

        return ret;
    }

    @Override
    public RecordSet getRecords(DataLoadingSpec spec) throws MalformedSourceException {
        return getRecords();
    }

    public void addDataLoader(DataLoader loader) {
        dataLoaders.add(loader);
    }

    /**
     * @return the dataLoaders
     */
    public List<DataLoader> getDataLoaders() {
        return dataLoaders;
    }

    /**
     * @param dataLoaders the dataLoaders to set
     */
    public void setDataLoaders(List<DataLoader> dataLoaders) {
        this.dataLoaders = dataLoaders;
    }
}
