package sg.edu.nus.comp.cs3219.viz.logic;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import sg.edu.nus.comp.cs3219.viz.common.datatransfer.AnalysisRequest;
import sg.edu.nus.comp.cs3219.viz.common.entity.PresentationSection;
import sg.edu.nus.comp.cs3219.viz.common.entity.record.AuthorRecord;
import sg.edu.nus.comp.cs3219.viz.common.entity.record.Exportable;
import sg.edu.nus.comp.cs3219.viz.common.entity.record.ReviewRecord;
import sg.edu.nus.comp.cs3219.viz.common.entity.record.SubmissionAuthorRecord;
import sg.edu.nus.comp.cs3219.viz.common.entity.record.SubmissionRecord;
import sg.edu.nus.comp.cs3219.viz.storage.repository.AuthorRecordRepository;
import sg.edu.nus.comp.cs3219.viz.storage.repository.SubmissionRecordRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class AnalysisLogic {

    private static final Logger log = Logger.getLogger(AnalysisLogic.class.getSimpleName());

    private static final Map<String, Class> DATABASE_FIELD_NAME_TO_TYPE_MAP = new HashMap<>();

    static {
        populateMapForClass(AuthorRecord.class);
        populateMapForClass(ReviewRecord.class);
        populateMapForClass(SubmissionRecord.class);
    }

    /**
     * Generates a map from field name to type so SQL query can be correctly generated.
     */
    private static void populateMapForClass(Class<?> classToExamine) {
        Arrays.stream(classToExamine.getDeclaredFields())
                .filter(f -> f.getAnnotation(Exportable.class) != null)
                .forEach(field ->
                        DATABASE_FIELD_NAME_TO_TYPE_MAP.put(field.getAnnotation(Exportable.class).nameInDB(), field.getType()));
    }

    private JdbcTemplate jdbcTemplate;

    private AuthorRecordRepository authorRecordRepository;

    private SubmissionRecordRepository submissionRecordRepository;

    public AnalysisLogic(JdbcTemplate jdbcTemplate,
                         AuthorRecordRepository authorRecordRepository,
                         SubmissionRecordRepository submissionRecordRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.authorRecordRepository = authorRecordRepository;
        this.submissionRecordRepository = submissionRecordRepository;
    }

    public List<Map<String, Object>> analyse(AnalysisRequest analysisRequest) {
        String sql = generateSQL(analysisRequest);

        log.info("Analysis Query: " + sql);
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> analyseCoauthorship(AnalysisRequest analysisRequest) {
        String dataSet = analysisRequest.getDataSet();
        Map<String, Object> extraData = analysisRequest.getExtraData();
        String collabType = extraData.get("collabType").toString();

        Logger log = Logger.getLogger(RecordLogic.class.getSimpleName());

        List<AuthorRecord> authorExisting = authorRecordRepository.findByDataSetEquals(dataSet);
        if (authorExisting == null) {
            log.info("authorRepo is null");
            return null;
        }

        List<SubmissionRecord> submissionExisting = submissionRecordRepository.findByDataSetEquals(dataSet);
        if (submissionExisting == null) {
            log.info("submissionRepo is null");
            return null;
        }

        Map<String, AuthorRecord> authorRecordMap = new HashMap<String, AuthorRecord>();

        authorExisting.stream().forEach(a -> {
            String firstName = a.getFirstName();
            String lastName = a.getLastName();
            String fullName = firstName + " " + lastName;
            authorRecordMap.put(fullName, a);
        });

        Map<String, Integer> collaboration = new HashMap<String, Integer>();

        submissionExisting.stream().forEach(s -> {
            List<SubmissionAuthorRecord> authorNames = s.getAuthorSet();

            if (authorNames != null && authorNames.size() > 1) {
                for (int i = 0; i < authorNames.size() - 1; i++) {

                    String authorNameOne = authorNames.get(i).getName();
                    if (authorRecordMap.get(authorNameOne) == null) {
                        continue;
                    }
                    String firstValue = "";

                    if (collabType.equals("country")) {
                        firstValue = authorRecordMap.get(authorNameOne).getCountry();
                    } else if (collabType.equals("organization")) {
                        firstValue = authorRecordMap.get(authorNameOne).getOrganisation();
                    }

                    int j = i + 1;
                    for (; j < authorNames.size(); j++) {
                        String authorNameTwo = authorNames.get(j).getName();
                        if (authorRecordMap.get(authorNameTwo) == null) {
                            continue;
                        }

                        String secondValue = "";
                        if (collabType.equals("country")) {
                            secondValue = authorRecordMap.get(authorNameTwo).getCountry();
                        } else if (collabType.equals("organization")) {
                            secondValue = authorRecordMap.get(authorNameTwo).getOrganisation();
                        }

                        List<String> tempListForSort = new ArrayList<>();
                        tempListForSort.add(firstValue);
                        tempListForSort.add(secondValue);
                        java.util.Collections.sort(tempListForSort);

                        String combinedValue = tempListForSort.get(0) + "-" + tempListForSort.get(1);

                        if (collaboration.containsKey(combinedValue)) {
                            int noOfCollaboration = collaboration.get(combinedValue);
                            noOfCollaboration++;
                            collaboration.put(combinedValue, noOfCollaboration);
                        } else {
                            collaboration.put(combinedValue, 1);
                        }
                    }
                }
            }
        });

        List<Map<String, Object>> result = new ArrayList<>();

        collaboration.forEach((key, num) -> {
            Map<String, Object> map = new HashMap<String, Object>() {{
                put(collabType, key);
                put("value", num);
            }};
            result.add(map);
        });

        return result;
    }

    private String generateSQL(AnalysisRequest analysisRequest) {
        String selectionsStr = analysisRequest.getSelections().stream()
                .map(s -> s.getExpression() + String.format(" AS `%s`", s.getRename()))
                .collect(Collectors.joining(","));
        if (selectionsStr.isEmpty()) {
            selectionsStr = "*";
        }

        String tablesStr = analysisRequest.getInvolvedRecords().stream()
                .map(PresentationSection.Record::getName)
                .collect(Collectors.joining(","));

        String joinersStr = analysisRequest.getJoiners().stream()
                .map(j -> String.format("%s = %s", j.getLeft(), j.getRight()))
                .collect(Collectors.joining(" AND "));

        String filtersStr = analysisRequest.getFilters().stream()
                .map(f -> String.format("%s %s %s", f.getField(), f.getComparator(), wrapValue(f.getField(), f.getValue())))
                .collect(Collectors.joining(" AND "));

        String dataSetFilter = analysisRequest.getInvolvedRecords().stream()
                .filter(r -> !r.isCustomized())
                .map(t -> String.format("%s.data_set = '%s'", t.getName(), analysisRequest.getDataSet()))
                .collect(Collectors.joining(" AND "));

        String groupersStr = analysisRequest.getGroupers().stream()
                .map(PresentationSection.Grouper::getField)
                .collect(Collectors.joining(","));

        String sortersStr = analysisRequest.getSorters().stream()
                .map(s -> String.format("%s %s", s.getField(), s.getOrder()))
                .collect(Collectors.joining(","));

        String baseSQL = String.format("SELECT %s FROM %s", selectionsStr, tablesStr);

        if (!dataSetFilter.isEmpty()) {
            baseSQL += String.format(" WHERE %s", dataSetFilter);
        } else {
            baseSQL += " WHERE true";
        }

        if (!joinersStr.isEmpty()) {
            baseSQL += String.format(" AND %s", joinersStr);
        }

        if (!filtersStr.isEmpty()) {
            baseSQL += String.format(" AND %s", filtersStr);
        }

        if (!groupersStr.isEmpty()) {
            baseSQL += String.format(" GROUP BY %s", groupersStr);
        }

        if (!sortersStr.isEmpty()) {
            baseSQL += String.format(" ORDER BY %s", sortersStr);
        }
        return baseSQL;
    }

    String wrapValue(String fieldName, String val) {
        Class fieldType = DATABASE_FIELD_NAME_TO_TYPE_MAP.get(fieldName);
        if (Integer.class.equals(fieldType) || int.class.equals(fieldType)
                || Double.class.equals(fieldType) || double.class.equals(fieldType)
                || Boolean.class.equals(fieldType) || boolean.class.equals(fieldType)) {
            return val;
        }
        return String.format("'%s'", val);
    }
}
