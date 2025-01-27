import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * MotocrossFantasyLeague is a console-based application for a fantasy motocross racing game.
 * Players predict race results and earn points based on the accuracy of their predictions.
 * 
 * Game Rules:
 * - Players predict 1st, 2nd, and 3rd place riders
 * - Players select a wildcard rider at a specific position (5-20)
 * - Points are awarded for correct predictions:
 *   - 1st place: 25 points
 *   - 2nd place: 23 points
 *   - 3rd place: 21 points
 *   - Wildcard rider: 50 points
 */
public class MotocrossFantasyLeague {

    /**
     * Main method to run the Motocross Fantasy League game.
     * Handles game flow: reading race results, collecting player predictions, 
     * calculating scores, and displaying the leaderboard.
     * 
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Initialize data structures to store race results and player information
        Map<Integer, String> raceResultMap = new HashMap<>();
        List<Player> players = new ArrayList<>();

        // Step 1: Read race results from an external file
        try {
            // Read race results from results.txt, mapping each line to a race position
            List<String> lines = Files.readAllLines(Paths.get("results.txt"));
            int position = 1;
            for (String rider : lines) {
                raceResultMap.put(position, rider.trim());
                position++;
            }
        } catch (IOException e) {
            // Handle file reading errors gracefully
            System.out.println("Error reading results.txt file: " + e.getMessage());
            return;
        }

        // Optional: Verify race results by displaying them
        System.out.println("Race Results:");
        for (Map.Entry<Integer, String> entry : raceResultMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        // Step 2: Collect predictions from players
        for (int i = 1; i <= 3; i++) {
            System.out.println("\nPlayer " + i + ": Enter your predictions.");
            
            // Collect top 3 rider predictions
            System.out.print("Enter your 1st place rider: ");
            String firstPlace = scanner.nextLine();
            System.out.print("Enter your 2nd place rider: ");
            String secondPlace = scanner.nextLine();
            System.out.print("Enter your 3rd place rider: ");
            String thirdPlace = scanner.nextLine();

            // Validate and collect wildcard position with robust input handling
            int wildcardPosition = getValidWildcardPosition(scanner);
            
            System.out.print("Enter your wildcard rider: ");
            String wildcardRider = scanner.nextLine();

            // Create a Player object with predictions and add to players list
            Player player = new Player("Player " + i, firstPlace, secondPlace, thirdPlace, wildcardPosition, wildcardRider);
            players.add(player);
        }

        // Step 3: Calculate player scores based on race results
        calculateScores(players, raceResultMap);

        // Step 4: Display game results and leaderboard
        displayLeaderboard(players);

        scanner.close();
    }

    /**
     * Validates and retrieves a valid wildcard position from user input.
     * Ensures the input is a number between 5 and 20.
     * 
     * @param scanner Input scanner to read user input
     * @return A valid wildcard position between 5 and 20
     */
    private static int getValidWildcardPosition(Scanner scanner) {
        while (true) {
            System.out.print("Enter your wildcard position (5-20): ");
            String input = scanner.nextLine();
        
            // Check if input is empty
            if (input.isEmpty()) {
                System.out.println("Wildcard position cannot be empty. Please enter a number between 5 and 20.");
                continue;
            }
        
            // Validate input is a number
            try {
                int wildcardPosition = Integer.parseInt(input);
        
                // Check if the number is in range
                if (wildcardPosition >= 5 && wildcardPosition <= 20) {
                    return wildcardPosition; // Valid input
                } else {
                    System.out.println("Invalid range. Please enter a number between 5 and 20.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    /**
     * Calculates scores for each player based on their race predictions.
     * Points are awarded for correctly predicting rider positions.
     * 
     * @param players List of players with their predictions
     * @param raceResults Map of race positions and corresponding riders
     */
    private static void calculateScores(List<Player> players, Map<Integer, String> raceResults) {
        for (Player player : players) {
            int score = 0;

            // Award points for top 3 position predictions
            if (player.getFirstPlace().equals(raceResults.get(1))) score += 25;
            if (player.getSecondPlace().equals(raceResults.get(2))) score += 23;
            if (player.getThirdPlace().equals(raceResults.get(3))) score += 21;

            // Award points for correct wildcard prediction
            if (player.getWildcardPosition() >= 5 && player.getWildcardPosition() <= 20) {
                String wildcardRider = raceResults.get(player.getWildcardPosition());
                if (wildcardRider != null && wildcardRider.equals(player.getWildcardRider())) {
                    score += 50;
                }
            }

            // Update player's total score
            player.setScore(score);
        }
    }

    /**
     * Displays the game leaderboard, sorted by player scores.
     * In case of a tie, the wildcard position is used as a tiebreaker.
     * 
     * @param players List of players to be ranked
     */
    private static void displayLeaderboard(List<Player> players) {
        // Sort players by score (descending) and use wildcard position for tie-breaking
        players.sort((p1, p2) -> {
            int scoreComparison = Integer.compare(p2.getScore(), p1.getScore());
            if (scoreComparison == 0) {
                return Integer.compare(p1.getWildcardPosition(), p2.getWildcardPosition());
            }
            return scoreComparison;
        });

        // Display overall leaderboard
        System.out.println("\nLeaderboard:");
        for (Player player : players) {
            System.out.println(player.getName() + ": " + player.getScore() + " points");
        }

        // Display podium (top 3 players)
        System.out.println("\nPodium:");
        for (int i = 0; i < Math.min(players.size(), 3); i++) {
            System.out.println((i + 1) + ". " + players.get(i).getName() + " with " + players.get(i).getScore() + " points");
        }
    }
}

/**
 * Represents a player in the Motocross Fantasy League.
 * Stores player name, race predictions, and score.
 */
class Player {
    private String name;
    private String firstPlace;
    private String secondPlace;
    private String thirdPlace;
    private int wildcardPosition;
    private String wildcardRider;
    private int score;

    /**
     * Constructs a new Player with race predictions.
     * 
     * @param name Player's name
     * @param firstPlace Predicted 1st place rider
     * @param secondPlace Predicted 2nd place rider
     * @param thirdPlace Predicted 3rd place rider
     * @param wildcardPosition Predicted wildcard position
     * @param wildcardRider Predicted wildcard rider
     */
    public Player(String name, String firstPlace, String secondPlace, String thirdPlace, int wildcardPosition, String wildcardRider) {
        this.name = name;
        this.firstPlace = firstPlace;
        this.secondPlace = secondPlace;
        this.thirdPlace = thirdPlace;
        this.wildcardPosition = wildcardPosition;
        this.wildcardRider = wildcardRider;
        this.score = 0;
    }

    // Getter methods for player's predictions and score
    public String getName() { return name; }
    public String getFirstPlace() { return firstPlace; }
    public String getSecondPlace() { return secondPlace; }
    public String getThirdPlace() { return thirdPlace; }
    public int getWildcardPosition() { return wildcardPosition; }
    public String getWildcardRider() { return wildcardRider; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
}