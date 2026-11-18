public class Node {

   private String name;
   private Node firstChild;
   private Node nextSibling;

   private static String input; // parsitav sõne
   private static int pos;      // mitmendat märki parajasti loeme

   Node (String n, Node d, Node r) {
      name = n;
      firstChild = d;
      nextSibling = r;
   }

   /** Ehitab puu parempoolsest suluesitusest, nt "(B,C)A", ja tagastab juure. */
   public static Node parsePostfix (String s) {
      input = s;
      pos = 0;
      if (s == null || s.isEmpty()) {
         error("sisend on tühi");
      }
      for (char c : s.toCharArray()) {
         if (Character.isWhitespace(c)) {
            error("puu ei tohi sisaldada tühikuid");
         }
      }
      Node root = readTree();
      if (pos < s.length()) {
         error("puul saab olla ainult üks juur, liigne osa algab kohalt " + pos);
      }
      return root;
   }

   /** Loeb ühe alampuu: kõigepealt sulgudes lapsed (kui on), siis tipu nimi. */
   private static Node readTree() {
      Node firstChild = null;

      if (current() == '(') {
         pos++;                         // jätame '(' vahele
         firstChild = readTree();       // esimene laps
         Node lastChild = firstChild;
         while (current() == ',') {
            pos++;                      // jätame ',' vahele
            lastChild.nextSibling = readTree();
            lastChild = lastChild.nextSibling;
         }
         if (current() != ')') {
            error("kohal " + pos + " oodati ',' või ')'");
         }
         pos++;                         // jätame ')' vahele
      }

      int start = pos;
      while (pos < input.length() && "(),".indexOf(input.charAt(pos)) < 0) {
         pos++;
      }
      if (start == pos) {
         error("kohal " + pos + " puudub tipu nimi");
      }
      String name = input.substring(start, pos);
      return new Node(name, firstChild, null);
   }

   /** Jooksev märk või 0, kui sõne on läbi. */
   private static char current() {
      if (pos < input.length()) {
         return input.charAt(pos);
      }
      return 0;
   }

   private static void error (String reason) {
      throw new RuntimeException("Vigane puu \"" + input + "\": " + reason);
   }

   /** Tagastab vasakpoolse suluesituse, nt "A(B,C)". */
   public String leftParentheticRepresentation() {
      StringBuilder sb = new StringBuilder(); // üks puhver kogu puu jaoks
      appendTo(sb);
      return sb.toString();
   }

   /** Kirjutab selle tipu ja tema alampuu etteantud puhvrisse. */
   private void appendTo (StringBuilder sb) {
      sb.append(name);
      if (firstChild != null) {
         sb.append('(');
         firstChild.appendTo(sb);
         Node child = firstChild.nextSibling;
         while (child != null) {
            sb.append(',');
            child.appendTo(sb);
            child = child.nextSibling;
         }
         sb.append(')');
      }
   }

   public static void main (String[] param) {
      String[] trees = { "(B1,C)A", "A", "((C,(E)D)B,F)A", "(((2,1)-,4)*,(69,3)/)+" };
      for (String s : trees) {
         Node t = Node.parsePostfix(s);
         System.out.println(s + " ==> " + t.leftParentheticRepresentation());
      }

      String[] wrong = { "A B", "(B,,C)A", "()A", "((C,D))A", "(B)A,(D)C" };
      for (String s : wrong) {
         try {
            Node.parsePostfix(s);
         } catch (RuntimeException e) {
            System.out.println(e.getMessage());
         }
      }
   }
}