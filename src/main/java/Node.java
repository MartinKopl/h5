public class Node {

   private String name;
   private Node firstChild;
   private Node nextSibling;

   private static String input;
   private static int pos;

   Node (String n, Node d, Node r) {
      name = n;
      firstChild = d;
      nextSibling = r;
   }

   public static Node parsePostfix (String s) {
      input = s;
      pos = 0;
      if (s == null || s.isEmpty()) {
         error("Input is empty");
      }
      for (char c : s.toCharArray()) {
         if (Character.isWhitespace(c)) {
            error("Tree can't contain whitespaces");
         }
      }
      Node root = readTree();
      if (pos < s.length()) {
         error("Tree can have only one root, excessive part starts at " + pos);
      }
      return root;
   }

   private static Node readTree() {
      Node firstChild = null;

      if (current() == '(') {
         pos++;
         firstChild = readTree();
         Node lastChild = firstChild;
         while (current() == ',') {
            pos++;
            lastChild.nextSibling = readTree();
            lastChild = lastChild.nextSibling;
         }
         if (current() != ')') {
            error("At " + pos + " was expected ',' or ')'");
         }
         pos++;
      }

      int start = pos;
      while (pos < input.length() && "(),".indexOf(input.charAt(pos)) < 0) {
         pos++;
      }
      if (start == pos) {
         error("At " + pos + " name of top is missing");
      }
      String name = input.substring(start, pos);
      return new Node(name, firstChild, null);
   }

   private static char current() {
      if (pos < input.length()) {
         return input.charAt(pos);
      }
      return 0;
   }

   private static void error (String reason) {
      throw new RuntimeException("Wrong tree \"" + input + "\": " + reason);
   }

   public String leftParentheticRepresentation() {
      StringBuilder sb = new StringBuilder(name);
      if (firstChild != null) {
         sb.append('(');
         sb.append(firstChild.leftParentheticRepresentation());
         Node child = firstChild.nextSibling;
         while (child != null) {
            sb.append(',');
            sb.append(child.leftParentheticRepresentation());
            child = child.nextSibling;
         }
         sb.append(')');
      }
      return sb.toString();
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