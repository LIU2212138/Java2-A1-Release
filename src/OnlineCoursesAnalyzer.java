import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 *
 * This is just a demo for you, please run it on JDK17
 * (some statements may be not allowed in lower version).
 * This is just a demo, and you can extend and implement functions
 * based on this demo, or implement it in a different way.
 */

public class OnlineCoursesAnalyzer {

    List<Course> courses = new ArrayList<>();

    public static void main(String[] args) {
        OnlineCoursesAnalyzer test = new OnlineCoursesAnalyzer("resources/local.csv");
    }

    public OnlineCoursesAnalyzer(String datasetPath) {
        BufferedReader br = null;
        String line;
        try {
            br = new BufferedReader(new FileReader(datasetPath, StandardCharsets.UTF_8));
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] info = line.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", -1);
                Course course = new Course(info[0], info[1], new Date(info[2]), info[3], info[4], info[5],
                        Integer.parseInt(info[6]),
                        Integer.parseInt(info[7]),
                        Integer.parseInt(info[8]),
                        Integer.parseInt(info[9]),
                        Integer.parseInt(info[10]),
                        Double.parseDouble(info[11]),
                        Double.parseDouble(info[12]),
                        Double.parseDouble(info[13]),
                        Double.parseDouble(info[14]),
                        Double.parseDouble(info[15]),
                        Double.parseDouble(info[16]),
                        Double.parseDouble(info[17]),
                        Double.parseDouble(info[18]),
                        Double.parseDouble(info[19]),
                        Double.parseDouble(info[20]),
                        Double.parseDouble(info[21]),
                        Double.parseDouble(info[22]));
                courses.add(course);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    
    //1
    public Map<String, Integer> getPtcpCountByInst() {
        TreeMap<String, Integer> result = new TreeMap<>(String::compareTo);
        for (Course course : courses) {
            result.merge(course.institution, course.participants, Integer::sum);
        }
        return result;
    }

    //2
    public Map<String, Integer> getPtcpCountByInstAndSubject() {
        Map<String, Integer> result = new LinkedHashMap<>();
        for (Course course : courses) {
            String instAndSub = course.institution + "-" + course.subject;
            result.merge(instAndSub, course.participants, Integer::sum);
        }
        result = result.entrySet().stream()
            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (s, s2) -> s,
                LinkedHashMap::new));
        result = result.entrySet().stream().sorted((e1, e2) -> {
            if (e1.getValue().equals(e2.getValue())) {
                return e1.getKey().compareTo(e2.getKey());
            } else {
                return 0;
            }
            })
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (s, s2) -> s,
                LinkedHashMap::new));
        return result;
    }

    //3
    public Map<String, List<List<String>>> getCourseListOfInstructor() {
        Map<String, List<List<String>>> result = new LinkedHashMap<>();
        for (Course course : courses) {
            String[] instructors = course.instructors.split(", ");
            if (instructors.length == 1) {
                if (result.containsKey(instructors[0])) {
                    result.get(instructors[0]).get(0).add(course.title);
                } else {
                    List<String> help1 = new ArrayList<>();
                    help1.add(course.title);
                    List<List<String>> res = new ArrayList<>();
                    res.add(help1);
                    res.add(new ArrayList<>());
                    result.put(instructors[0], res);
                }
            } else if (instructors.length > 1) {
                for (String instructor : instructors) {
                    if (result.containsKey(instructor)) {
                        result.get(instructor).get(1).add(course.title);
                    } else {
                        List<String> help1 = new ArrayList<>();
                        help1.add(course.title);
                        List<List<String>> res = new ArrayList<>();
                        res.add(new ArrayList<>());
                        res.add(help1);
                        result.put(instructor, res);
                    }
                }
            }
        }
        for (Entry<String, List<List<String>>> entry : result.entrySet()) {
            List<String> temp1 = entry.getValue().get(0).stream().distinct()
                .sorted(String::compareTo).toList();
            List<String> temp2 = entry.getValue().get(1).stream().distinct()
                .sorted(String::compareTo).toList();
            List<List<String>> listList = new ArrayList<>();
            listList.add(temp1);
            listList.add(temp2);
            entry.setValue(listList);
        }
        return result;
    }

    //4
    public List<String> getCourses(int topK, String by) {
        List<String> stringList = new ArrayList<>();
        if (by.equals("hours")) {
            Map<String, Double> titleTime = new LinkedHashMap<>();
            for (Course course : courses) {
                if (titleTime.containsKey(course.title)) {
                    titleTime.put(course.title,
                        Math.max(titleTime.get(course.title), course.totalHours));
                } else {
                    titleTime.put(course.title, course.totalHours);
                }
            }
            titleTime = titleTime.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (s, s2) -> s,
                    LinkedHashMap::new));
            stringList = titleTime.entrySet().stream().sorted((e1, e2) -> {
                    if (e1.getValue().equals(e2.getValue())) {
                        return e1.getKey().compareTo(e2.getKey());
                    } else {
                        return 0;
                    }
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (s, s2) -> s,
                    LinkedHashMap::new)).keySet().stream().limit(topK).toList();
        } else if (by.equals("participants")) {
            Map<String, Integer> titlePart = new LinkedHashMap<>();
            for (Course course : courses) {
                if (titlePart.containsKey(course.title)) {
                    titlePart.put(course.title, Math.max(titlePart.get(course.title), course.participants));
                } else {
                    titlePart.put(course.title, course.participants);
                }
            }
            titlePart = titlePart.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (s, s2) -> s,
                    LinkedHashMap::new));
            stringList = titlePart.entrySet().stream().sorted((e1, e2) -> {
                    if (e1.getValue().equals(e2.getValue())) {
                        return e1.getKey().compareTo(e2.getKey());
                    } else {
                        return 0;
                    }
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (s, s2) -> s,
                    LinkedHashMap::new)).keySet().stream().limit(topK).toList();
        }
        return stringList;
    }

    //5
    public List<String> searchCourses(String courseSubject, double percentAudited, double totalCourseHours) {
        Map<String, Vector<Double>> threeTable = new LinkedHashMap<>();
        List<String> result = new ArrayList<>();
        for (Course course : courses) {
            if (course.subject.toLowerCase(Locale.ROOT).contains(courseSubject.toLowerCase(Locale.ROOT))) {
                if (course.percentAudited >= percentAudited) {
                    if (course.totalHours <= totalCourseHours) {
                        result.add(course.title);
                    }
                }
            }
        }
        result = result.stream().distinct().sorted(String::compareTo).toList();
        return result;
    }

    //6
    public List<String> recommendCourses(int age, int gender, int isBachelorOrHigher) {
        Map<String, List<Double>> idInfoTable = new LinkedHashMap<>();
        Map<String, String> idTitleTable = new LinkedHashMap<>();
        Map<String, Date> idDateTable = new LinkedHashMap<>();
        for (Course course : courses) {
            if (!idInfoTable.containsKey(course.number)) {
                List<Double> info = new ArrayList<>();
                info.add(course.medianAge);
                info.add(course.percentMale);
                info.add(course.percentDegree);
                info.add(1d);
                idInfoTable.put(course.number, info);
            } else {
                List<Double> helper = idInfoTable.get(course.number);
                idInfoTable.get(course.number).set(0, helper.get(0) + course.medianAge);
                idInfoTable.get(course.number).set(1, helper.get(1) + course.percentMale);
                idInfoTable.get(course.number).set(2, helper.get(2) + course.percentDegree);
                idInfoTable.get(course.number).set(3, helper.get(3) + 1);
            }
            if (!idTitleTable.containsKey(course.number)) {
                idTitleTable.put(course.number, course.title);
                idDateTable.put(course.number, course.launchDate);
            } else {
                if (idDateTable.get(course.number).before(course.launchDate)) {
                    idTitleTable.replace(course.number, course.title);
                    idDateTable.replace(course.number, course.launchDate);
                }
            }
        }
        Map<String, Double> idSimTable = new LinkedHashMap<>();
        for (Entry<String, List<Double>> entry : idInfoTable.entrySet()) {
            List<Double> info = entry.getValue();
            double averageAge = (double) info.get(0) / info.get(3);
            double averageMale = (double) info.get(1) / info.get(3);
            double averageBachelor = (double) info.get(2) / info.get(3);
            double similarityvalue = Math.pow(age - averageAge, 2)
                + Math.pow(gender * 100 - averageMale, 2)
                + Math.pow(isBachelorOrHigher * 100 - averageBachelor, 2);
            idSimTable.put(entry.getKey(), similarityvalue);
        }

        idSimTable = idSimTable.entrySet().stream().sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (s, s2) -> s,
                LinkedHashMap::new));
        List<String> numberList = idSimTable.entrySet().stream().sorted((e1, e2) -> {
            if (e1.getValue().equals(e2.getValue())) {
                return idTitleTable.get(e1.getKey()).compareTo(idTitleTable.get(e2.getKey()));
            } else {
                return 0;
            }
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (s, s2) -> s,
            LinkedHashMap::new)).keySet().stream().toList();
        List<String> titleList = new ArrayList<>();
        for (String num : numberList) {
            titleList.add(idTitleTable.get(num));
        }
        titleList = titleList.stream().distinct().limit(10).toList();


        return titleList;
    }

}

class Course {
    String institution;
    String number;
    Date launchDate;
    String title;
    String instructors;
    String subject;
    int year;
    int honorCode;
    int participants;
    int audited;
    int certified;
    double percentAudited;
    double percentCertified;
    double percentCertified50;
    double percentVideo;
    double percentForum;
    double gradeHigherZero;
    double totalHours;
    double medianHoursCertification;
    double medianAge;
    double percentMale;
    double percentFemale;
    double percentDegree;

    public Course(String institution, String number, Date launchDate,
                  String title, String instructors, String subject,
                  int year, int honorCode, int participants,
                  int audited, int certified, double percentAudited,
                  double percentCertified, double percentCertified50,
                  double percentVideo, double percentForum, double gradeHigherZero,
                  double totalHours, double medianHoursCertification,
                  double medianAge, double percentMale, double percentFemale,
                  double percentDegree) {
        this.institution = institution;
        this.number = number;
        this.launchDate = launchDate;
        if (title.startsWith("\"")) {
            title = title.substring(1);
        }
        if (title.endsWith("\"")) {
            title = title.substring(0, title.length() - 1);
        }
        this.title = title;
        if (instructors.startsWith("\"")) {
            instructors = instructors.substring(1);
        }
        if (instructors.endsWith("\"")) {
            instructors = instructors.substring(0, instructors.length() - 1);
        }
        this.instructors = instructors;
        if (subject.startsWith("\"")) {
            subject = subject.substring(1);
        }
        if (subject.endsWith("\"")) {
            subject = subject.substring(0, subject.length() - 1);
        }
        this.subject = subject;
        this.year = year;
        this.honorCode = honorCode;
        this.participants = participants;
        this.audited = audited;
        this.certified = certified;
        this.percentAudited = percentAudited;
        this.percentCertified = percentCertified;
        this.percentCertified50 = percentCertified50;
        this.percentVideo = percentVideo;
        this.percentForum = percentForum;
        this.gradeHigherZero = gradeHigherZero;
        this.totalHours = totalHours;
        this.medianHoursCertification = medianHoursCertification;
        this.medianAge = medianAge;
        this.percentMale = percentMale;
        this.percentFemale = percentFemale;
        this.percentDegree = percentDegree;
    }
}
