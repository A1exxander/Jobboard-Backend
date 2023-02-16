package io.Jobboard.DB;

public class QueryValidator {

    public static boolean validQueryArgs(final String[] arg){ // Not very good atm but will suffice - Look to improve, possibly by taking each of the args, taking the current string and all possible substrings inserting it into a set. Then loop thru a blacklist & check if blacklisted characters or substrings are present

        for (int i = 0; i < arg.length; i++){

            final int midpoint = arg[i].length() / 2;
            int left = midpoint;
            int right = midpoint;
            while (left >= 0 || right < arg[i].length()){ // Improved w two pointer approach, not binary since were not dividing the problem in half each time
                if (left >= 0 && (arg[i].charAt(left) == ' ' || arg[i].charAt(left) == '\'')){
                    return false;
                }
                if (right < arg[i].length() && (arg[i].charAt(right) == ' ' || arg[i].charAt(right) == '\'')){
                    return false;
                }
                --left;
                ++right;
            }

        }

        return true;

    }

    public static boolean validQueryCDesc(String arg){

        final int midpoint = arg.length() / 2;
        int left = midpoint;
        int right = midpoint;

            while (left >= 0 || right < arg.length()){
                if (left >= 0 && (arg.charAt(left) == '\"' || arg.charAt(right) != '\'')){ // Our desc can have spaces, so lets not check for that.
                    return false;
                }
                if (right < arg.length() && (arg.charAt(right) == '\"' || arg.charAt(right) == '\'')){
                    return false;
                }
                --left;
                ++right;
            }

        return true;

    }

}
