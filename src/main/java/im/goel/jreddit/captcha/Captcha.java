package im.goel.jreddit.captcha;

import im.goel.jreddit.user.User;
import im.goel.jreddit.utils.Utils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class corresponds to the reddit's captcha class.
 *
 * @author Karan Goel
 * @author Raul Rene Lepsa
 */
public class Captcha {

    private static final String IMAGE_FORMAT = "png";
    private static final String IMAGE_PATH = "captcha." + IMAGE_FORMAT;

    /**
     * Generates and saves a new reddit captcha in the working directory
     * @param user user to get captcha for
     *
     * @return the iden of the generated captcha as a String
     */
	public String newCaptcha(User user) {
		String iden = null;

		try {
            // Get the captcha iden
			JSONObject obj = Utils.post("", new URL("http://www.reddit.com/api/new_captcha"), user.getCookie());
            iden = (String) ((JSONArray) ((JSONArray) ((JSONArray) obj.get("jquery")).get(11)).get(3)).get(0);
            System.out.println("Received CAPTCHA iden: " + iden);

            // Get the corresponding captcha image
            URL url = new URL("http://www.reddit.com/captcha/" + iden + ".png");
            RenderedImage captcha = ImageIO.read(url);

            // Write the file to disk
            ImageIO.write(captcha, IMAGE_FORMAT, new File(IMAGE_PATH));

        } catch (MalformedURLException e) {
            System.out.println("Invalid URL for retrieving captcha");
        } catch (IOException e) {
            System.out.println("Error reading captcha file");
		}

		return iden;
	}

    /**
     * Check whether user needs CAPTCHAs for API methods that define the "captcha" and "iden" parameters.
     * @param user user to do the check for
     *
     * @return true if CAPTCHAs are needed, false otherwise
     */
    public boolean needsCaptcha(User user) {
        boolean needsCaptcha = false;

        try {
            needsCaptcha = (Boolean) Utils.get(new URL("http://www.reddit.com/api/needs_captcha.json"), user.getCookie());
        } catch (MalformedURLException e) {
            System.out.println("Error verifying if the user needs a captcha. The URL is invalid");
        }

        return needsCaptcha;
    }

}
