package gr.ekt.bteio.specs;

import gr.ekt.bte.core.DataOutputSpec;

public class DSpaceOutputSpec implements DataOutputSpec {
    private String prefix_dir_;
    private int padding_;

    /**
     * @return the padding_
     */
    public int getPadding() {
        return padding_;
    }

    /**
     * @param padding_ the padding_ to set
     */
    public void setPadding(int padding) {
        this.padding_ = padding;
    }

    /**
     * @return the prefix_dir_
     */
    public String getPrefixDir() {
        return prefix_dir_;
    }

    /**
     * @param prefix_dir_ the prefix_dir_ to set
     */
    public void setPrefixDir(String prefix_dir) {
        this.prefix_dir_ = prefix_dir;
    }

}
