package org.example;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TableCell;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.example.Items.Ball;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ConfigReader {

	/**
	 * You will probably not want to use a static method/class for this.
	 *
	 * This is just an example of how to access different parts of the json
	 *
	 * @param path The path of the json file to read
	 */
	private static Canvas canvas = new Canvas();

	private static Vector<Ball> balls = new Vector<>();

	private static double Friction;
	public static void parse(String path) {
		JSONParser parser = new JSONParser();
		try {
			Object object = parser.parse(new FileReader(path));

			// convert Object to JSONObject
			JSONObject jsonObject = (JSONObject) object;

			// reading the Table section:
			JSONObject jsonTable = (JSONObject) jsonObject.get("Table");

			// reading a value from the table section
			String tableColour = (String) jsonTable.get("colour");

			// reading a coordinate from the nested section within the table
			// note that the table x and y are of type Long (i.e. they are integers)
			Long tableX = (Long) ((JSONObject) jsonTable.get("size")).get("x");
			Long tableY = (Long) ((JSONObject) jsonTable.get("size")).get("y");

			// getting the friction level.
			// This is a double which should affect the rate at which the balls slow down

            Friction = (Double) jsonTable.get("friction");
			canvas = new Canvas(tableX, tableY);
			GraphicsContext gc = canvas.getGraphicsContext2D();

			gc.setFill(Color.valueOf(tableColour));
			gc.fillRect(0, 0, tableX, tableY);


			// reading the "Balls" section:
			JSONObject jsonBalls = (JSONObject) jsonObject.get("Balls");

			// reading the "Balls: ball" array:
			JSONArray jsonBallsBall = (JSONArray) jsonBalls.get("ball");

			// reading from the array:
			for (Object obj : jsonBallsBall) {
				JSONObject jsonBall = (JSONObject) obj;

				// the ball colour is a String
				String colour = (String) jsonBall.get("colour");

				// the ball position, velocity, mass are all doubles
				Double positionX = (Double) ((JSONObject) jsonBall.get("position")).get("x");
				Double positionY = (Double) ((JSONObject) jsonBall.get("position")).get("y");

				Double velocityX = (Double) ((JSONObject) jsonBall.get("velocity")).get("x");
				Double velocityY = (Double) ((JSONObject) jsonBall.get("velocity")).get("y");

				Double mass = (Double) jsonBall.get("mass");

				balls.add(new Ball(positionX, positionY, velocityX, velocityY, mass, colour));
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public static double getFriction() {
		return Friction;
	}

	public static Vector<Ball> getBalls() {
		return balls;
	}

	public static Canvas getCanvas() {
		return canvas;
	}
	/**
	 * Your main method will probably be in another file!
	 * 
	 * @param args First argument is the path to the config file
	 */
	public static void main() {
		// if a command line argument is provided, that should be used as the path
		// if not, you can hard-code a default. e.g. "src/main/resources/config.json"
		// this makes it easier to test your program with different config files
		String configPath = "src/main/resources/config.json";
		// parse the file:
		ConfigReader.parse(configPath);
	}

}
