import java.util.Random;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Classe Bataille contenant toutes les procédures requises pour le fonctionnement du jeu
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
	
	/**Permet de générer des nombres aléatoires*/
	public static Random rand = new Random();
	
	/**Permet de lire les entrées au clavier*/
	public static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	
	/**Permet d'afficher un message spécial si l'entrée est incorrecte*/
	public static boolean erreur = false;
	
	/**Énumération contenant les deux orientations possibles d'un bateau dans une grille*/
	enum Direction
	{
		HORIZONTAL,
		VERTICAL
	}
	
	/**
	 * Fonction main qui rend le fichier exécutable
	 */
	public static void main(String [] args)
	{
		engagement();
	}
	
	/**
	 * Fonction principale qui permet de démarrer la partie
	 */
	public static void engagement()
	{
		boolean fini = false; //Permet de boucler sur l'entrée de coordonnées tant que la partie n'est pas finie
		int ligne; //Ligne pour le tir
		
		String lettre; //Lettre de la colonne du tir. Sera convertie en chiffre pour "colonne"
		int colonne; //Colonne du tir
		
		//System.out.println(grilleOrdi[9][0]);
		remplirGrillesDeZeros();
		
		//grilleOrdi[9][0] = 1; //DEBUG
		initGrilleOrdi();
		
		//afficherGrille(grilleOrdi);
		
		initGrilleJeu();
		
		//À partir d'ici, les deux grilles sont initialisées et le jeu commence
		
		while(!fini)
		{
			
			//DEBUG AFFICHE GRILLE ORDINATEUR
			//afficherGrille(grilleOrdi);
			
			//Le joueur tire
			System.out.println("À votre tour!");
			
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
					
					//Finit ici si l'entrée est incorrecte
				default:
					colonne = -1;
					System.out.println("Entrée incorrecte.");
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
					System.out.println("Entrée incorrecte.");
				}
			}
			while(ligne > 9 || ligne < 0);
			
			//Donne le résultat du tir
			mouvement(grilleOrdi, ligne, colonne);
			
			//Vérifie si tous les bateaux de la grille adverse sont coulés
			if(vainqueur(grilleOrdi))
			{
				//Termine immédiatement la boucle et met fin au jeu
				fini = true;
				System.out.println("Bravo, vous avez gagné!");
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
	 * Assure que les grilles soient prêtes à être remplies
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
	 * Procédure qui permet d'afficher une grille à la console
	 * 
	 * @param grille Grille qui sera affichée
	 */
	static void afficherGrille(int [][] grille)
	{
		//Identifiant de la ligne
		int ligne = 1;
		//Caractères 
		System.out.println("   A B C D E F G H I J");
		
		for(int i = 0; i < 10; i++)
		{
			//Ce if permet de retirer un espace devant la ligne 10 afin que la grille soit bien alignée
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
	 * Fonction qui valide si un bateau peut être placé sur une grille passée en paramètre selon sa position, sa longueur,
	 * sa direction et le contenu de la grille
	 * 
	 * @param grille Grille sur laquelle sera placé le bateau
	 * @param ligne Ligne de la première case du bateau
	 * @param colonne Colonne de la première case du bateau
	 * @param direction Direction du bateau, soit horisontal ou vertical
	 * @param type Longueur du bateau
	 * @return Un booléen qui indique si le bateau peut être placé à l'endroit souhaité
	 */
	public static boolean posOk(int [][] grille, int ligne, int colonne, Direction direction, int type)
	{
		if(direction == Direction.HORIZONTAL)
		{
			if(colonne + type > 9)
			{
				erreur = true;
				//Le bateau dépasse de la grille, la position n'est donc pas OK
				return false;
			}
			else
			{
				for(int i = colonne; i <= colonne + type; i ++)
				{
					if(grille[ligne][i] != 0)
					{
						erreur = true;
						//Si les cases souhaitées ne sont pas vides, position pas OK
						return false;
					}
				}
			}
		}
		else //Direction sera forcément verticale
		{
			if(ligne + type > 9)
			{				
				erreur = true;
				//Le bateau dépasse de la grille, la position n'est donc pas OK
				return false;
			}
			else
			{
				for(int i = ligne; i <= ligne + type; i ++)
				{
					if(grille[i][colonne] != 0)
					{
						erreur = true;
						//Si les cases souhaitées ne sont pas vides, position pas OK
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
	 * Procédure qui initialise la grille de jeu de l'ordinateur et place les bateaux dedans
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
			//Change la longueur du bateau selon son numéro
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
			
			//La position étant correcte, on place le bateau
			if(direction == Direction.HORIZONTAL)
			{
				for(int j = colonne; j < colonne + longueur; j ++)
				{
					//Les cases occupées par le bateau ont son nombre dedans
					grilleOrdi[ligne][j] = i;
				}
			}
			else
			{
				for(int j = ligne; j < ligne + longueur; j ++)
				{
					//Les cases occupées par le bateau ont son nombre dedans
					grilleOrdi[j][colonne] = i;
				}
			}
			
		}

	}
	
	/**
	 * Fonction permettant de générer un nombre aléatoire entre a inclus et b exclus
	 * 
	 * @param a Début de la plage de nombres
	 * @param b Fin exclue de la plage de nombres
	 * @return Entier généré aléatoirement
	 */
	public static int randRange(int a, int b)
	{
		return rand.nextInt(b-a)+a;
	}

	/**
	 * Procédure qui fait remplir la grilleJoueur à l'utilisateur
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
			
			//Change la longueur du bateau selon son numéro
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
			
			//Pose les questions sur la position du bateau à l'utilisateur
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
						
						//Finit ici si l'entrée est incorrecte
					default:
						colonne = -1;
						System.out.println("Entrée incorrecte.");
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
						System.out.println("Entrée incorrecte.");
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
						System.out.println("Entrée incorrecte.");
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
			
			//La position étant correcte, on place le bateau
			if(direction == Direction.HORIZONTAL)
			{
				for(int j = colonne; j < colonne + longueur; j ++)
				{
					//Les cases occupées par le bateau ont son nombre dedans
					grilleJoueur[ligne][j] = i;
				}
			}
			else
			{
				for(int j = ligne; j < ligne + longueur; j ++)
				{
					//Les cases occupées par le bateau ont son nombre dedans
					grilleJoueur[j][colonne] = i;
				}
			}
		}
		
		System.out.println("Bateaux placés avec succès!");
		System.out.println("Votre grille :");
		afficherGrille(grilleJoueur);
	}
	
	/**
	 * Permet de lire les entrées clavier de l'utilisateur
	 * 
	 * @return Un String contenant l'entrée de l'utilisateur
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
	 * @param s String à vérifier
	 * @return Un booléen qui confirme si le nombre est entier
	 */
	public static boolean isInt(String s)
	{
		return s.matches("\\d+");
	}
	
	/**
	 * Fonction permettant de lire un entier au clavier
	 * 
	 * @return L'entier entré par l'utilisateur
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
	 * Vérifie si un bateau est coulé (n'est plus présent dans la grille)
	 * 
	 * @param grille Grille dans laquelle la vérification est effectuée
	 * @param bateau Bateau qui est vérifié
	 * @return Un booléen qui sera vrai si un bateau n'apparait plus dans la grille
	 */
	public static boolean couler(int [][] grille, int bateau)
	{
		for(int i = 0; i < 10; i++)
		{
			for(int j = 0; j < 10; j ++)
			{
				if(grille[i][j] == bateau)
				{
					//La fonction indique que le bateau n'est pas coulé lorsqu'elle trouve au moins une fois son numéro dans la grille
					return false;
				}
			}
		}
		
		//Le bateau a été coulé; son chiffre n'apparait pas dans la grille
		return true;
	}

	
	/**
	 * Procédure indiquant le résultat d'un tir dans une grille
	 * 
	 * @param grille Grille dans laquelle le tir sera vérifié
	 * @param ligne Ligne du tir
	 * @param colonne Colonne du tir
	 */
	public static void mouvement(int [][] grille, int ligne, int colonne)
	{
		int bateauTouche; //Stocke le nombre d'un bateau s'il est touché
		String nomBateau = ""; //Permet de fournir le nom d'un bateau quand il est coulé
		
		bateauTouche = grille[ligne][colonne];
		
		if(bateauTouche == 0)
		{
			System.out.println("À l'eau!");
		}
		else
		{
			//Inscrit que le bateau est touché dans la grille
			grille[ligne][colonne] = 6;
			
			//Si le bateau est coulé, on prend son nom et on l'affiche
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
				
				System.out.println("Bateau " + nomBateau + " Coulé!");
			}
			else
			{
				//Le bateau n'est pas coulé
				System.out.println("Touché");
			}
		}
	}
	
	/**
	 * Fonction qui retourne un tableau qui contient les coordonnées du tir aléatoire de l'ordinateur
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
	 * Fonction qui vérifie une grille pour savoir si tous les bateaux sont coulés
	 * 
	 * @param grille Grille à vérifier
	 * @return Un booléen qui sera vrai si tous les bateaux de la grille sont coulés
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
