import java.util.Random;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Classe Bataille contenant toutes les proc�dures requises pour le fonctionnement du jeu
 * 
 * @author Christophe Verreault
 * @version 1.0
 */
public class Bataille
{
	/**Grille contenant les informations sur les bateaux de l'ordinateu*/
	public static int [][] grilleOrdi = new int [10][10];
	
	/**Grille contenant les informations sur les bateaux du joueur*/
	public static int [][] grilleJoueur = new int [10][10];
	
	/**Permet de g�n�rer des nombres al�atoires*/
	public static Random rand = new Random();
	
	/**Permet de lire les entr�es au clavier*/
	public static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	
	/**Permet d'afficher un message sp�cial si l'entr�e est incorrecte*/
	public static boolean erreur = false;
	
	/**�num�ration contenant les deux orientations possibles d'un bateau dans une grille*/
	enum Direction
	{
		HORIZONTAL,
		VERTICAL
	}
	
	/**
	 * Fonction main qui rend le fichier ex�cutable
	 */
	public static void main(String [] args)
	{
		engagement();
	}
	
	/**
	 * Fonction principale qui permet de d�marrer la partie
	 */
	public static void engagement()
	{
		boolean fini = false; //Permet de boucler sur l'entr�e de coordonn�es tant que la partie n'est pas finie
		int ligne; //Ligne pour le tir
		
		String lettre; //Lettre de la colonne du tir. Sera convertie en chiffre pour "colonne"
		int colonne; //Colonne du tir
		
		//System.out.println(grilleOrdi[9][0]);
		remplirGrillesDeZeros();
		
		//grilleOrdi[9][0] = 1; //DEBUG
		initGrilleOrdi();
		
		//afficherGrille(grilleOrdi);
		
		initGrilleJeu();
		
		//� partir d'ici, les deux grilles sont initialis�es et le jeu commence
		
		while(!fini)
		{
			
			//DEBUG AFFICHE GRILLE ORDINATEUR
			//afficherGrille(grilleOrdi);
			
			//Le joueur tire
			System.out.println("� votre tour!");
			
			//Le joueur entre la colonne
			do
			{
				System.out.println("Entrez la colonne (A - J) : ");
				
				//Lecture de la lettre
				lettre = readString();
				
				//Change la lettre en chiffre
				switch(lettre)
				{
				case "a":
				case "A":
					colonne = 0;
					break;
				case "b":
				case "B":
					colonne = 1;
					break;
				case "c":
				case "C":
					colonne = 2;
					break;
				case "d":
				case "D":
					colonne = 3;
					break;
				case "e":
				case "E":
					colonne = 4; 
					break;
				case "f":
				case "F":
					colonne = 5;
					break;
				case "g":
				case "G":
					colonne = 6;
					break;
				case "h":
				case "H":
					colonne = 7;
					break;
				case "i":
				case "I":
					colonne = 8;
					break;
				case "j":
				case "J":
					colonne = 9;
					break;
					
					//Finit ici si l'entr�e est incorrecte
				default:
					colonne = -1;
					System.out.println("Entr�e incorrecte.");
				}
			}while(colonne == -1);
			
			//Le joueur entre la ligne
			do
			{
				System.out.println("Entrez la ligne (1 - 10) : ");
				ligne = readInt();
				ligne --;
				
				if(ligne > 9 || ligne < 0)
				{
					System.out.println("Entr�e incorrecte.");
				}
			}
			while(ligne > 9 || ligne < 0);
			
			//Donne le r�sultat du tir
			mouvement(grilleOrdi, ligne, colonne);
			
			//V�rifie si tous les bateaux de la grille adverse sont coul�s
			if(vainqueur(grilleOrdi))
			{
				//Termine imm�diatement la boucle et met fin au jeu
				fini = true;
				System.out.println("Bravo, vous avez gagn�!");
				break;
			}
			
			//L'ordinateur tire
			System.out.println("L'ordinateur tire");
			
			int [] tir = tirOrdinateur();
			
			mouvement(grilleJoueur, tir[0], tir[1]);
			
			if(vainqueur(grilleJoueur))
			{
				fini = true;
				System.out.println("L'ordinateur gagne.");
				break;
			}
		}
	}
	
	/**
	 * Remplit les grilles de l'ordinateur et du joueur avec des 0.
	 * Assure que les grilles soient pr�tes � �tre remplies
	 */
	public static void remplirGrillesDeZeros()
	{
		for(int i = 0; i < 10; i ++)
		{
			for(int j = 0; j < 10; j ++)
			{
				grilleOrdi[i][j] = 0;
				grilleJoueur[i][j] = 0;
			}
		}
	}
	
	/**
	 * Proc�dure qui permet d'afficher une grille � la console
	 * 
	 * @param grille Grille qui sera affich�e
	 */
	static void afficherGrille(int [][] grille)
	{
		//Identifiant de la ligne
		int ligne = 1;
		//Caract�res 
		System.out.println("   A B C D E F G H I J");
		
		for(int i = 0; i < 10; i++)
		{
			//Ce if permet de retirer un espace devant la ligne 10 afin que la grille soit bien align�e
			if(ligne < 10)
			{
				System.out.print(" " + ligne + " ");
			}
			else
			{
				System.out.print(ligne + " ");
			}
			
			ligne ++;
			
			for(int j = 0; j < 10; j ++)
			{
				System.out.print(grille[i][j] + " ");
			}
			//Retour de ligne
			System.out.println();
		}
	}
	
	/**
	 * Fonction qui valide si un bateau peut �tre plac� sur une grille pass�e en param�tre selon sa position, sa longueur,
	 * sa direction et le contenu de la grille
	 * 
	 * @param grille Grille sur laquelle sera plac� le bateau
	 * @param ligne Ligne de la premi�re case du bateau
	 * @param colonne Colonne de la premi�re case du bateau
	 * @param direction Direction du bateau, soit horisontal ou vertical
	 * @param type Longueur du bateau
	 * @return Un bool�en qui indique si le bateau peut �tre plac� � l'endroit souhait�
	 */
	public static boolean posOk(int [][] grille, int ligne, int colonne, Direction direction, int type)
	{
		if(direction == Direction.HORIZONTAL)
		{
			if(colonne + type > 9)
			{
				erreur = true;
				//Le bateau d�passe de la grille, la position n'est donc pas OK
				return false;
			}
			else
			{
				for(int i = colonne; i <= colonne + type; i ++)
				{
					if(grille[ligne][i] != 0)
					{
						erreur = true;
						//Si les cases souhait�es ne sont pas vides, position pas OK
						return false;
					}
				}
			}
		}
		else //Direction sera forc�ment verticale
		{
			if(ligne + type > 9)
			{				
				erreur = true;
				//Le bateau d�passe de la grille, la position n'est donc pas OK
				return false;
			}
			else
			{
				for(int i = ligne; i <= ligne + type; i ++)
				{
					if(grille[i][colonne] != 0)
					{
						erreur = true;
						//Si les cases souhait�es ne sont pas vides, position pas OK
						return false;
					}
				}
			}
		}
		
		erreur = false;
		
		//Si tous les calculs sont valides, la position est OK
		return true;
	}
	
	/**
	 * Proc�dure qui initialise la grille de jeu de l'ordinateur et place les bateaux dedans
	 */
	public static void initGrilleOrdi()
	{
		//Longueur (en nombre de cases) du bateau
		int longueur = 0;
		int ligne;
		int colonne;
		Direction direction;
		
		//Boucle 5 fois, une fois par bateau
		for(int i = 1; i <= 5; i ++)
		{
			//Change la longueur du bateau selon son num�ro
			switch(i)
			{
			case 1:
				longueur = 5;
				break;
			case 2:
				longueur = 4;
				break;
			case 3:
				longueur = 3;
				break;
			case 4:
				longueur = 3;
				break;
			case 5:
				longueur = 2;
				break;
			}
			
			do
			{
				//Choisit la direction du bateau au hasard (direction 1 ou 2)
				if(randRange(1,3) == 1)
				{
					direction = Direction.HORIZONTAL;
				}
				else
				{
					direction = Direction.VERTICAL;
				}
				
				//Choisit la ligne et la colonne du bateau au hasard
				ligne = randRange(0,10);
				colonne = randRange(0,10);
			}
			while(!posOk(grilleOrdi, ligne, colonne, direction, longueur));
			
			//La position �tant correcte, on place le bateau
			if(direction == Direction.HORIZONTAL)
			{
				for(int j = colonne; j < colonne + longueur; j ++)
				{
					//Les cases occup�es par le bateau ont son nombre dedans
					grilleOrdi[ligne][j] = i;
				}
			}
			else
			{
				for(int j = ligne; j < ligne + longueur; j ++)
				{
					//Les cases occup�es par le bateau ont son nombre dedans
					grilleOrdi[j][colonne] = i;
				}
			}
			
		}

	}
	
	/**
	 * Fonction permettant de g�n�rer un nombre al�atoire entre a inclus et b exclus
	 * 
	 * @param a D�but de la plage de nombres
	 * @param b Fin exclue de la plage de nombres
	 * @return Entier g�n�r� al�atoirement
	 */
	public static int randRange(int a, int b)
	{
		return rand.nextInt(b-a)+a;
	}

	/**
	 * Proc�dure qui fait remplir la grilleJoueur � l'utilisateur
	 */
	public static void initGrilleJeu()
	{
		//Longueur (en nombre de cases) du bateau
		int longueur = 0;
		int ligne = -1;
		
		String lettre;
		int colonne = -1;
		
		String nomBateau = "";
		Direction direction = Direction.HORIZONTAL;
		
		
		
		//Boucle 5 fois, une fois par bateau
		for(int i = 1; i <= 5; i ++)
		{
			System.out.println("Votre grille : ");
			afficherGrille(grilleJoueur);
			
			//Change la longueur du bateau selon son num�ro
			switch(i)
			{
			case 1:
				longueur = 5;
				nomBateau = "Porte-Avions (5 cases)";
				break;
			case 2:
				longueur = 4;
				nomBateau = "Croiseur (4 cases)";
				break;
			case 3:
				longueur = 3;
				nomBateau = "Contre-Torpilleur (3 cases)";
				break;
			case 4:
				longueur = 3;
				nomBateau = "Sous-Marin (3 cases)";
				break;
			case 5:
				longueur = 2;
				nomBateau = "Torpilleur (2 cases)";
				break;
			}
			
			//Pose les questions sur la position du bateau � l'utilisateur
			do
			{
				if(erreur)
				{
					System.out.println("Erreur : Le bateau ne rentre pas dans la grille OU est par-dessus un autre!");
				}
				
				
				//Lecture de la lettre
				do
				{
					System.out.println("Donnez la lettre (a - j, minuscule ou majuscule) pour le " + nomBateau + " :");
					//Lecture de la lettre
					lettre = readString();
					
					switch(lettre)
					{
					case "a":
					case "A":
						colonne = 0;
						break;
					case "b":
					case "B":
						colonne = 1;
						break;
					case "c":
					case "C":
						colonne = 2;
						break;
					case "d":
					case "D":
						colonne = 3;
						break;
					case "e":
					case "E":
						colonne = 4; 
						break;
					case "f":
					case "F":
						colonne = 5;
						break;
					case "g":
					case "G":
						colonne = 6;
						break;
					case "h":
					case "H":
						colonne = 7;
						break;
					case "i":
					case "I":
						colonne = 8;
						break;
					case "j":
					case "J":
						colonne = 9;
						break;
						
						//Finit ici si l'entr�e est incorrecte
					default:
						colonne = -1;
						System.out.println("Entr�e incorrecte.");
					}
				}
				while(colonne == -1);
					
				//Lecture du nombre
				do
				{
					System.out.println("Donnez le nombre (1 - 10) pour le " + nomBateau);
					ligne = readInt();
					ligne --;
					
					if(ligne > 9 || ligne < 0)
					{
						System.out.println("Entr�e incorrecte.");
					}
				}
				while(ligne > 9 || ligne < 0);
				
				//Lecture de la direction
				int dir;
				do
				{
					System.out.println("Donnez la direction (1 - Horizontal, 2 - Vertical) pour le " + nomBateau);
					dir = readInt();
					
					if(dir < 1 || dir > 2)
					{
						System.out.println("Entr�e incorrecte.");
					}
					else
					{
						if(dir == 1)
						{
							direction = Direction.HORIZONTAL;
						}
						else
						{
							direction = Direction.VERTICAL;
						}
					}
				}
				while(dir < 1 || dir > 2);
			}
			while(!posOk(grilleJoueur, ligne, colonne, direction, longueur));
			
			//La position �tant correcte, on place le bateau
			if(direction == Direction.HORIZONTAL)
			{
				for(int j = colonne; j < colonne + longueur; j ++)
				{
					//Les cases occup�es par le bateau ont son nombre dedans
					grilleJoueur[ligne][j] = i;
				}
			}
			else
			{
				for(int j = ligne; j < ligne + longueur; j ++)
				{
					//Les cases occup�es par le bateau ont son nombre dedans
					grilleJoueur[j][colonne] = i;
				}
			}
		}
		
		System.out.println("Bateaux plac�s avec succ�s!");
		System.out.println("Votre grille :");
		afficherGrille(grilleJoueur);
	}
	
	/**
	 * Permet de lire les entr�es clavier de l'utilisateur
	 * 
	 * @return Un String contenant l'entr�e de l'utilisateur
	 */
	public static String readString()
	{
		String res = "";
		try
		{
			res = br.readLine();
		}
		catch(Exception e)
		{
			System.out.print("y a un bug avec le read");
		}
		return res;
	}
	
	/**
	 * Permet de savoir si un String ne contient qu'un seul entier
	 * 
	 * @param s String � v�rifier
	 * @return Un bool�en qui confirme si le nombre est entier
	 */
	public static boolean isInt(String s)
	{
		return s.matches("\\d+");
	}
	
	/**
	 * Fonction permettant de lire un entier au clavier
	 * 
	 * @return L'entier entr� par l'utilisateur
	 */
	public static int readInt ()
	{
		while(true)
		{
			String s = readString();
			if(isInt(s))return Integer.parseInt(s);
		}
	}
	
	/**
	 * V�rifie si un bateau est coul� (n'est plus pr�sent dans la grille)
	 * 
	 * @param grille Grille dans laquelle la v�rification est effectu�e
	 * @param bateau Bateau qui est v�rifi�
	 * @return Un bool�en qui sera vrai si un bateau n'apparait plus dans la grille
	 */
	public static boolean couler(int [][] grille, int bateau)
	{
		for(int i = 0; i < 10; i++)
		{
			for(int j = 0; j < 10; j ++)
			{
				if(grille[i][j] == bateau)
				{
					//La fonction indique que le bateau n'est pas coul� lorsqu'elle trouve au moins une fois son num�ro dans la grille
					return false;
				}
			}
		}
		
		//Le bateau a �t� coul�; son chiffre n'apparait pas dans la grille
		return true;
	}

	
	/**
	 * Proc�dure indiquant le r�sultat d'un tir dans une grille
	 * 
	 * @param grille Grille dans laquelle le tir sera v�rifi�
	 * @param ligne Ligne du tir
	 * @param colonne Colonne du tir
	 */
	public static void mouvement(int [][] grille, int ligne, int colonne)
	{
		int bateauTouche; //Stocke le nombre d'un bateau s'il est touch�
		String nomBateau = ""; //Permet de fournir le nom d'un bateau quand il est coul�
		
		bateauTouche = grille[ligne][colonne];
		
		if(bateauTouche == 0)
		{
			System.out.println("� l'eau!");
		}
		else
		{
			//Inscrit que le bateau est touch� dans la grille
			grille[ligne][colonne] = 6;
			
			//Si le bateau est coul�, on prend son nom et on l'affiche
			if(couler(grille, bateauTouche))
			{
				switch(bateauTouche)
				{
				case 1:
					nomBateau = "Porte-Avions";
					break;
				case 2:
					nomBateau = "Croiseur";
					break;
				case 3:
					nomBateau = "Contre-Torpilleur";
					break;
				case 4:
					nomBateau = "Sous-Marin";
					break;
				case 5:
					nomBateau = "Torpilleur";
					break;
				}
				
				System.out.println("Bateau " + nomBateau + " Coul�!");
			}
			else
			{
				//Le bateau n'est pas coul�
				System.out.println("Touch�");
			}
		}
	}
	
	/**
	 * Fonction qui retourne un tableau qui contient les coordonn�es du tir al�atoire de l'ordinateur
	 * 
	 * @return un tableau contenant la ligne et la colonne du tir de l'ordinateur
	 */
	public static int[] tirOrdinateur()
	{
		int ligne = randRange(0,10);
		int colonne = randRange(0,10);
		
		int[] tableau = {ligne, colonne}; 
		
		return tableau;
	}
	
	/**
	 * Fonction qui v�rifie une grille pour savoir si tous les bateaux sont coul�s
	 * 
	 * @param grille Grille � v�rifier
	 * @return Un bool�en qui sera vrai si tous les bateaux de la grille sont coul�s
	 */
	public static boolean vainqueur(int [][] grille)
	{
		for(int i = 0; i < 10; i ++)
		{
			for(int j = 0; j < 10; j ++)
			{
				if(grille[i][j] != 0 && grille[i][j] != 6)
				{
					return false;
				}
			}
		}
		
		return true;
	}
}
