public class Node {

   private String name;
   private Node firstChild;
   private Node nextSibling;

   private static String input; // string being parsed
   private static int pos;      // index of the current character

   Node (String n, Node d, Node r) {
      name = n;
      firstChild = d;
      nextSibling = r;
   }

   /** Builds a tree from its right parenthetic representation, e.g. "(B,C)A", and returns the root. */
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

   /** Reads one subtree: first the children in brackets (if any), then the node name. */
   private static Node readTree() {
      Node firstChild = null;

      if (current() == '(') {
         pos++;                         // skip '('
         firstChild = readTree();       // first child
         Node lastChild = firstChild;
         while (current() == ',') {
            pos++;                      // skip ','
            lastChild.nextSibling = readTree();
            lastChild = lastChild.nextSibling;
         }
         if (current() != ')') {
            error("kohal " + pos + " oodati ',' või ')'");
         }
         pos++;                         // skip ')'
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

   /** Returns the current character, or 0 at the end of the string. */
   private static char current() {
      if (pos < input.length()) {
         return input.charAt(pos);
      }
      return 0;
   }

   private static void error (String reason) {
      throw new RuntimeException("Vigane puu \"" + input + "\": " + reason);
   }

   /** Returns the left parenthetic representation, e.g. "A(B,C)". */
   public String leftParentheticRepresentation() {
      StringBuilder sb = new StringBuilder(); // one buffer for the whole tree
      appendTo(sb);
      return sb.toString();
   }

   /** Appends this node and its subtree to the given buffer. */
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

   /** Returns the tree as pseudo-XML, where <Lk> marks a node at depth k. */
   public String toXML() {
      StringBuilder sb = new StringBuilder(); // one buffer for the whole tree
      appendXML(sb, 1);
      return sb.toString();
   }

   /** Appends this node (at the given depth) and its subtree as pseudo-XML. */
   private void appendXML (StringBuilder sb, int depth) {
      if (depth > 1) {
         sb.append('\n');                    // every node except the root starts on a new line
      }
      appendIndent(sb, depth);
      sb.append("<L").append(depth).append("> ").append(name);

      if (firstChild == null) {             // leaf: closing tag on the same line
         sb.append(" </L").append(depth).append('>');
         return;
      }

      Node child = firstChild;
      while (child != null) {
         child.appendXML(sb, depth + 1);
         child = child.nextSibling;
      }
      sb.append('\n');
      appendIndent(sb, depth);
      sb.append("</L").append(depth).append('>');
   }

   /** Appends 4 spaces for every level below the root. */
   private static void appendIndent (StringBuilder sb, int depth) {
      for (int i = 1; i < depth; i++) {
         sb.append("    ");
      }
   }

   public static void main (String[] param) {
      String xmlExample = "((C)B,(E,F)D,G)A";
      System.out.println(xmlExample + " ==>");
      System.out.println(Node.parsePostfix(xmlExample).toXML());
      System.out.println(Node.parsePostfix("A").toXML());

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