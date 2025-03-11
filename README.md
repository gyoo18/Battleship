# Battleship
Le jeu classique à jouer entre amis, sur votre ordinateur.

Ceci est ma soumission au défi de février du REI/UQODE

## Comment jouer?
### Télécharger
Téléchargez la dernière version dans la section [*Releases*](https://github.com/gyoo18/Battleship/releases) et décompressez le fichier. Dans le dossier bin, exécutez le fichier **Battleship.bat** sur Windows et **Battleship** sur Linux/MacOs.

Ce jeu se joue en multijoueur en LAN, un-contre-un. Afin de commencer à jouer, le jeu devra se connecter à un autre joueur (cette étape peut prendre 
jusqu'à 25 secondes). Assurez-vous d'être connecté sur *le même réseau* que votre compétiteur et que vous pouvez l'atteindre en LAN.

Si vous voulez tester la connexion à votre compétiteur, suivez les instructions de la section **Tester la connexion**.

Il est à noter que le jeu peux se connecter à lui-même, en lançant deux instance sur le même ordinateur.

### Déroulement du jeu.
Le jeu ne démarera pas tant qu'il n'aurat pas trouvé un adversaire. Une autre instance du jeu (sur un autre ordinateur ou sur le vôtre) doit être partie et connectée en LAN pour pouvoir commencer la partie.

Vous serez alors face à un plateau sur lequel figure vos bateaux ainsi qu'un radar. Appuyez sur CTRL et bouger la souris pour orbiter autour du plateau et roulez la molette de souris pour zoomer/dézoomer.
Cliquez sur vos bateaux pour les déplacer et faites un clique droit pour les tourner de 90°. Lorsque vous aurez terminé de placer vos bateaux, appuyez sur espace pour commencer la partie. La partie débuttera lorsque l'adversaire aura lui aussi appuyé sur espace.

Lors de votre tour, le radar se relèvera et vous pourrez cliquer dessus pour tirer sur l'ennemi. Une pine blanche apparaîtra alors si vous avez manqué votre cible et une pine rouge apparaîtra si vous avez touché quelque chose. Si vous avez touché toutes les cases d'un des bateaux de votre adversaire, le jeu indiquera que vous l'avez coulé.

Après votre tour, vous pourrez assister à la frappe de votre adversaire. Appuyez sur espace pour passer à votre tour à nouveau.

Le joueur qui aura coulé tout les bateaux de l'adversaire aura gagné.

## Construire (Build)
### Windows : 
À l'intérieur du dossier parent du projet :
```
gradlew.bat build
gradlew.bat run
```
### Linux/MacOS :
À l'intérieur du dossier parent du projet :
```
$ ./gradlew build
$ ./gradlew run
```

## Structure :
Ce projet fait usage de la LWJGL pour faire interface avec GLFW et OpenGL et utilise l'API *Socket* de Java pour faire la communication TCP/IP.
![diagramme d'entité-relation](/README/ERD.png)

## Tester la connexion

Ouvrez une invite de commande sur l'un des deux ordinateurs et tapez `ifconfig` sur Linux/MacOS et `ipconfig` sur Windows. Cherchez une addresse ip commençant par l'une des suites de chiffres suivantes : `192.168.XX.XX`,`172.16.XX.XX` ou `10.0.XX.XX` (à noter que le troisième chiffre doit être le même sur les deux ordinateurs, il arrive souvent sur des réseaux public que cela ne soit pas le cas, auquel cas la connexion est impossible). Sur l'autre ordinateur, ouvrez une nouvelle invite de commande et tapez `ping`, suivit de l'addresse ip du premier ordinateur. Si la connexion est impossible, rien ne se passera, tandis que si la connexion se fait, de nouvelles lignes s'afficheront périodiquement.
