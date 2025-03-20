# Battleship
Le jeu classique à jouer entre amis, sur votre ordinateur.

Ceci est ma soumission au défi de février du REI/UQODE

![Exemple de jeu 1](/README/Exemple1.png)
![Exemple de jeu 2](/README/Exemple2.png)
![Exemple de jeu 3](/README/Exemple3.png)

## Comment jouer?
### Télécharger
Téléchargez la dernière version dans la section [*Releases*](https://github.com/gyoo18/Battleship/releases) et décompressez le fichier. Dans le dossier bin, exécutez le fichier **Battleship.bat** sur Windows et **Battleship** sur Linux/MacOs.

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
