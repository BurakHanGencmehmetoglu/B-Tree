import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class CengTreeParser
{
    public static ArrayList<CengVideo> parseVideosFromFile(String filename)
    {
        ArrayList<CengVideo> videoList = new ArrayList<>();
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader(filename));
            String line = bufferedReader.readLine();
            while (line != null) {
                String[] tokens = line.split("[|]");
                CengVideo cengVideo = new CengVideo(Integer.parseInt(tokens[0]),tokens[1],tokens[2],tokens[3]);
                videoList.add(cengVideo);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
        }catch (IOException e) {
            e.printStackTrace();
        }


        return videoList;
    }

    public static void startParsingCommandLine() throws IOException
    {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            String[] tokens = input.split("[|]");
            tokens[0] = tokens[0].toLowerCase();
            if (tokens[0].equals("quit")) {
                break;
            }
            else if (tokens[0].equals("print")) {
                CengVideoRunner.printTree();
            }
            else {
                if (tokens[0].equals("add")) {
                    CengVideo cengVideo = new CengVideo(Integer.parseInt(tokens[1]), tokens[2], tokens[3], tokens[4]);
                    CengVideoRunner.addVideo(cengVideo);
                }
                else if (tokens[0].equals("search")){
                    CengVideoRunner.searchVideo(Integer.parseInt(tokens[1]));
                }
            }
        }
        System.exit(0);
    }
}