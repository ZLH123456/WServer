import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AsciiInputStream extends FilterInputStream{

    public AsciiInputStream(InputStream fi){
        super(fi);
    }

    public int read(){

        int character = -1;

        try{
            character = in.read(); //We need to pass in for it to work

            while(character == '<') {

                while(character != '>'){

                    character = in.read();

                    if (character == '!'){

                        long tmp = in.skip(2); //HTML Comment are like <!-- comment --> So if we skip 2 position after ! we will skip --
                        character = (int) tmp;

                        while(character != '-'){

                            character = in.read();
                        }
                    }
                }

                character = in.read();
            }
        } catch (IOException IOErr){
            IOErr.printStackTrace();
        }

        return character;
    }

}
