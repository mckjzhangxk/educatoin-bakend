package tech.mathai.app.Entity;

import lombok.Data;

/**
 * Created by mathai on 20-10-12.
 */
@Data
public class AnalysisDto {
    private String catalog;
    private String result;

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
