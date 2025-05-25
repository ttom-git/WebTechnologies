package it.beans;

public enum Grade {
    ABSENT,
    FAILED,
    REJECTED,
    RETRIED,
    LAUDE,
    GRADE_18,
    GRADE_19,
    GRADE_20,
    GRADE_21,
    GRADE_22,
    GRADE_23,
    GRADE_24,
    GRADE_25,
    GRADE_26,
    GRADE_27,
    GRADE_28,
    GRADE_29,
    GRADE_30;
	
	// moved @ InsertResultServlet.java
	/*public static Grade parseGrade(String s) {
	    switch (s.toLowerCase()) {
	        case "absent": return ABSENT;
	        case "rejected": return REJECTED;
	        case "retried": return RETRIED;
	        case "laude": return LAUDE;
	        default:
	            try {
	                int n = Integer.parseInt(s);
	                return Grade.valueOf("GRADE_" + n);
	            } catch (Exception e) {
	                throw new IllegalArgumentException("Invalid grade: " + s);
	            }
	    }
	}*/ 
	
	public String toString() {
        if (this.name().startsWith("GRADE_")) {
            return this.name().substring(6);  //should hopefully work like: "GRADE_30" -> "30"
        }
        return this.name().toLowerCase(); //se non è tra quelli esplodo :)
    }
}



