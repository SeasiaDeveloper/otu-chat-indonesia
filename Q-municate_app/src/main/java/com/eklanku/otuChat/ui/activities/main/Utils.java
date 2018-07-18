package com.eklanku.otuChat.ui.activities.main;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormatSymbols;

public class Utils {
    public static String DoubleToCurency(long x) {
        String s = Long.toString(x);
        String r = "";
        for (int i = 1; i < s.length() + 1; ++i) {
            r = s.substring(s.length() - i, s.length() - i + 1) + r;
            if ((i % 3 == 0) && (i != s.length())) {
                r = "." + r;
            }
        }
        return r;// +"," + Float. .substring(2);
    }

    public static String StringToCurency(String x) {
        String s = x;
        String r = "";
        for (int i = 1; i < s.length() + 1; ++i) {
            r = s.substring(s.length() - i, s.length() - i + 1) + r;
            if ((i % 3 == 0) && (i != s.length())) {
                r = "." + r;
            }
        }
        return r.replace(".,", ",");// +"," + Float. .substring(2);
    }

    public static String md5(String input) {
        String result = input;
        if (input != null) {
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("MD5");
                md.update(input.getBytes());
                BigInteger hash = new BigInteger(1, md.digest());
                result = hash.toString(16);
                if ((result.length() % 2) != 0) {
                    result = "0" + result;
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return null;
            }
        }
        return result;
    }

    public static String md5new(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));

            return hexString.toString();
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getMonth(int month) {
        return new DateFormatSymbols().getShortMonths()[month - 1];
    }

    public static boolean isNotNull(String txt) {
        return txt != null && txt.trim().length() > 0 ? true : false;
    }

    public static String TambahTitik(String norek) {
        if ((norek.indexOf(".") == -1) && (norek.length() >= 13)) {
            norek = norek.substring(0, 3) + "." + norek.substring(3, 5) + "." + norek.substring(5, 11) + "."
                    + norek.substring(11, 13);
        } else if ((norek.indexOf(".") == -1) && (norek.length() >= 12)) {
            norek = norek.substring(0, 2) + "." + norek.substring(2, 4) + "." + norek.substring(4, 10) + "."
                    + norek.substring(10, 12);
        }
        return norek;
    }

    public static String FakturCount(String fmt, String value) {
        int val = Integer.parseInt(value) + 1;
        int l = fmt.length();
        String s = fmt + val;
        s = s.substring(s.length() - l, s.length());
        return s;
    }

    public static String centersamadengan(String paramString, int paramInt) {
        String str = "";
        int i = paramString.length();
        if (i <= paramInt) {
            int j = paramInt - i;
            for (int k = 0; k < j / 2; k++) {
                str = str + "=";
            }
            str = str + paramString;
            int l = paramInt - str.length();
            for (int k = 0; k < l; k++) {
                str = str + "=";
            }
            return str + "\n";
        }
        return str;
    }

    public static String centermin(String paramString, int paramInt) {
        String str = "";
        int i = paramString.length();
        if (i <= paramInt) {
            int j = paramInt - i;
            for (int k = 0; k < j / 2; k++) {
                str = str + "-";
            }
            str = str + paramString;
            int l = paramInt - str.length();
            for (int k = 0; k < l; k++) {
                str = str + "-";
            }
            return str + "\n";
        }
        return str;
    }

    public static String centerpagar(String paramString, int paramInt) {
        String str = "";
        int i = paramString.length();
        if (i <= paramInt) {
            int j = paramInt - i;
            for (int k = 0; k < j / 2; k++) {
                str = str + "#";
            }
            str = str + paramString;
            int l = paramInt - str.length();
            for (int k = 0; k < l; k++) {
                str = str + "#";
            }
            return str + "\n";
        }
        return str;
    }

    public static String center(String paramString, int paramInt) {
        String str = "";
        int i = paramString.length();
        if (i <= paramInt) {
            int j = paramInt - i;
            for (int k = 0; k < j / 2; k++)
                str = str + " ";
            return str + paramString + "\n";
        }
        return paramString;
    }

    public static String kolomkanan(String paramString) {
        if (paramString.length() > 57) {
            paramString = paramString.substring(0, 19) + "\n             " + paramString.substring(19, 38)
                    + "\n             " + paramString.substring(38, 57) + "\n             " + paramString.substring(57);
        } else if (paramString.length() > 38) {
            paramString = paramString.substring(0, 19) + "\n             " + paramString.substring(19, 38)
                    + "\n             " + paramString.substring(38);
        } else if (paramString.length() > 19) {
            paramString = paramString.substring(0, 19) + "\n             " + paramString.substring(19);
        }
        return paramString + "\n";
    }

    public static String leftRight(String paramString1, String paramString2, int paramInt) {
        int i = paramString1.length();
        int j = paramString2.length();
        int k = i + j;
        if (k <= paramInt) {
            String str2 = "" + paramString1;
            int n = paramInt - k;
            for (int i1 = 0; i1 < n; i1++)
                str2 = str2 + " ";
            return str2 + paramString2;
        }
        String str1 = "" + paramString1 + "\n";
        for (int m = 0; m < paramInt - j; m++)
            str1 = str1 + " ";
        return str1 + paramString2;
    }

    public static String fontMelebar16() {
        String str = convertHexToString("1B2120");
        return str;
    }

    public static String fontMeninggi32() {
        String str = convertHexToString("1B2110");
        return str;
    }

    public static String fontMemperbesar16() {
        byte[] cmd = new byte[3];
        cmd[0] = 0x1b;
        cmd[1] = 0x21;
        cmd[2] = 0x10;
        cmd[2] |= 0x20;
        String str = "";
        str = String.format("%02x", cmd[0]) + String.format("%02x", cmd[1]) + String.format("%02x", cmd[2]);
        str = convertHexToString(str);
        return str;
    }

    public static String fontBold21() {
        byte[] cmd = new byte[3];
        cmd[0] = 0x1b;
        cmd[1] = 0x21;
        cmd[2] = 0x10;
        cmd[2] |= 0x20;
        cmd[2] |= 0x1;
        String str = "";
        str = String.format("%02x", cmd[0]) + String.format("%02x", cmd[1]) + String.format("%02x", cmd[2]);
        str = convertHexToString(str);
        return str;
    }

    public static String fontNormal32() {
        String str = convertHexToString("1B2100");
        return str;
    }

    private static String convertHexToString(String hex) {
        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < hex.length() - 1; i += 2) {

            String output = hex.substring(i, (i + 2));
            int decimal = Integer.parseInt(output, 16);
            sb.append((char) decimal);
            temp.append(decimal);
        }
        return sb.toString();
    }

    public static String logoMobilePrinter(String MerkPrinter) {
        String logo = "";
        if (MerkPrinter.equals("Zonerich") || MerkPrinter.equals("RNF/BKT58")) {
            byte[] CMD_LOGO_AB = {0x1C, 0x70, 0x01, 0x30};
            logo = String.format("%02x", CMD_LOGO_AB[0]) + String.format("%02x", CMD_LOGO_AB[1])
                    + String.format("%02x", CMD_LOGO_AB[2]) + String.format("%02x", CMD_LOGO_AB[3]);
            logo = convertHexToString(logo);
        } else if (MerkPrinter.equals("Sunphor")) {
            byte[] CMD_LOGO_SP58 = {0x1F, 0x43, 0x02, 0x00, 0x0D};
            logo = String.format("%02x", CMD_LOGO_SP58[0]) + String.format("%02x", CMD_LOGO_SP58[1])
                    + String.format("%02x", CMD_LOGO_SP58[2]) + String.format("%02x", CMD_LOGO_SP58[3])
                    + String.format("%02x", CMD_LOGO_SP58[4]);
            logo = convertHexToString(logo);
        } else if (MerkPrinter.equals("Blue Bamboo P25")) {
            byte[] CMD_LOGO_P25 = {0x1B, 0x66, 0x00};
            logo = String.format("%02x", CMD_LOGO_P25[0]) + String.format("%02x", CMD_LOGO_P25[1])
                    + String.format("%02x", CMD_LOGO_P25[2]);
            logo = convertHexToString(logo);
        }
        return logo;
    }

    public static String toJsonStringPDAM(String uid, String pid, String produk, String idpel, String idpel2, String idpel3, String idtrx) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("uid", uid);
            obj.put("pid", pid);
            obj.put("produk", produk);
            obj.put("idpel", idpel);
            obj.put("idpel2", idpel2);
            obj.put("idpel3", idpel3);
            obj.put("idtrx", idtrx);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }

	public static String responseMessage(int code, String msgError){

		String message;

		switch (code) {
			case 502:
				message="Bad Gateway\n" + trimMessage(msgError, "errorMessage");
				break;
			case 405:
				message="Method Not Allowed\n" + trimMessage(msgError, "errorMessage");
				break;
			case 400:
				message="Bad Request\n" + trimMessage(msgError, "errorMessage");
				break;
			case 408:
				message="Request Timeout\n" + trimMessage(msgError, "errorMessage");
				break;
			case 409:
				message="Conflict\n" + trimMessage(msgError, "errorMessage");
				break;
			case 413:
				message="Request Entity Too Large\n" + trimMessage(msgError, "errorMessage");
				break;
			case 504:
				message="Gateway Timeout\n" + trimMessage(msgError, "errorMessage");
				break;
			case 403:
				message="Forbidden\n" + trimMessage(msgError, "errorMessage");
				break;
			case 410:
				message="Gone\n" + trimMessage(msgError, "errorMessage");
				break;
			case 500:
				message="Internal Server Error\n" + trimMessage(msgError, "errorMessage");
				break;
			case 411:
				message="Length Required\n" + trimMessage(msgError, "errorMessage");
				break;
			case 301:
				message="Moved Permanently\n" + trimMessage(msgError, "errorMessage");
				break;
			case 302:
				message="Temporary Redirect\n" + trimMessage(msgError, "errorMessage");
				break;
			case 300:
				message="Multiple Choices\n" + trimMessage(msgError, "errorMessage");
				break;
			case 406:
				message="Not Acceptable\n" + trimMessage(msgError, "errorMessage");
				break;
			case 404:
				message="Not Found\n" + trimMessage(msgError, "errorMessage");
				break;
			case 501:
				message="Not Implemented\n" + trimMessage(msgError, "errorMessage");
				break;
			case 304:
				message="Not Modified\n" + trimMessage(msgError, "errorMessage");
				break;
			case 402:
				message="Payment Required\n" + trimMessage(msgError, "errorMessage");
				break;
			case 412:
				message="Precondition Failed\n" + trimMessage(msgError, "errorMessage");
				break;
			case 407:
				message="Proxy Authentication Required\n" + trimMessage(msgError, "errorMessage");
				break;
			case 414:
				message="Request-URI Too Large\n" + trimMessage(msgError, "errorMessage");
				break;
			case 401:
				message="Unauthorized\n" + trimMessage(msgError, "errorMessage");
				break;
			case 503:
				message="Service Unavailable\n" + trimMessage(msgError, "errorMessage");
				break;
			case 415:
				message="Unsupported Media Type\n" + trimMessage(msgError, "errorMessage");
				break;
			case 305:
				message="Use Proxy\n" + trimMessage(msgError, "errorMessage");
				break;
			case 505:
				message="HTTP Version Not Supported\n" + trimMessage(msgError, "errorMessage");
				break;
			case 303:
				message="See Other\n" + trimMessage(msgError, "errorMessage");
				break;
			case 205:
				message="Reset Content\n" + trimMessage(msgError, "errorMessage");
				break;
			case 203:
				message= "Non-Authoritative Information\n" + trimMessage(msgError, "errorMessage");
				break;
			default:
				message="Unknow";
				break;
		}

		return message;
	}

    /*public static String responseMessage(int code, String msgError) {

        String message;

        switch (code) {
            case 502:
                message = trimMessage(msgError, "errorMessage");
                break;
            case 405:
                message = trimMessage(msgError, "errorMessage");
                break;
            case 400:
                message = trimMessage(msgError, "errorMessage");
                break;
            case 408:
                message = trimMessage(msgError, "errorMessage");
                break;
            case 409:
                message = trimMessage(msgError, "errorMessage");
                break;
            case 413:
                message = trimMessage(msgError, "errorMessage");
                break;
            case 504:
                message = trimMessage(msgError, "errorMessage");
                break;
            case 403:
                message = trimMessage(msgError, "errorMessage");
                break;
            case 410:
                message = trimMessage(msgError, "errorMessage");
                break;
            case 500:
                message = trimMessage(msgError, "errorMessage");
                break;
            case 411:
                message = trimMessage(msgError, "errorMessage");
                break;
            case 301:
                message = trimMessage(msgError, "errorMessage");
                break;
            case 302:
                message = trimMessage(msgError, "errorMessage");
                break;
            case 300:
                message = trimMessage(msgError, "errorMessage");
                break;
            case 406:
                message = trimMessage(msgError, "errorMessage");
                break;
            case 404:
                message = trimMessage(msgError, "errorMessage");
                break;
            case 501:
                message = trimMessage(msgError, "errorMessage");
                break;
            case 304:
                message = trimMessage(msgError, "errorMessage");
                break;
            case 402:
                message = trimMessage(msgError, "errorMessage");
                break;
            case 412:
                message = trimMessage(msgError, "errorMessage");
                break;
            case 407:
                message = trimMessage(msgError, "errorMessage");
                break;
            case 414:
                message = trimMessage(msgError, "errorMessage");
                break;
            case 401:
                message = trimMessage(msgError, "errorMessage");
                break;
            case 503:
                message = trimMessage(msgError, "errorMessage");
                break;
            case 415:
                message = trimMessage(msgError, "errorMessage");
                break;
            case 305:
                message = trimMessage(msgError, "errorMessage");
                break;
            case 505:
                message = trimMessage(msgError, "errorMessage");
                break;
            case 303:
                message = trimMessage(msgError, "errorMessage");
                break;
            case 205:
                message = trimMessage(msgError, "errorMessage");
                break;
            case 203:
                message = trimMessage(msgError, "errorMessage");
                break;
            default:
                message = "Internal server error";
                break;
        }

        return message;
    }*/

    private static String trimMessage(String json, String key) {
        String trimmedString;

        try {
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return "Internal server error";
        }

        return trimmedString;
    }
}
