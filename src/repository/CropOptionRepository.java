package repository;

import model.CropOption;

public class CropOptionRepository extends JsonRepository<CropOption> {

    private static final String FILE_PATH = "src/data/crop_options.json";

    public CropOptionRepository() {
        super(FILE_PATH, CropOption.class);
    }

    @Override
    protected String getId(CropOption cropOption) {
        return cropOption.getId();
    }

    public void save(CropOption cropOption) {
        findById(cropOption.getId()).ifPresentOrElse(
                existing -> update(cropOption),
                () -> add(cropOption)
        );
    }
}
