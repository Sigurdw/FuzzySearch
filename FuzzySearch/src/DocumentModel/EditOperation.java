package DocumentModel;

public enum EditOperation {
    Insert,
    Delete,
    Substitution,  //depricated
    Match;

    public static boolean isOperationAllowed(EditOperation previousEdit, EditOperation editOperation){
        boolean allowed = true;
        if(previousEdit == EditOperation.Delete && editOperation == EditOperation.Insert){
            allowed = false;
        }

        if(editOperation == EditOperation.Substitution){
            allowed = false;
        }

        return allowed;
    }

    public static int getOperationCost(EditOperation previousEdit, EditOperation editOperation){
        int cost;

        if(previousEdit == EditOperation.Insert && editOperation == EditOperation.Delete){
            cost = 0;
        }
        else{
            cost = getOperationCost(editOperation);
        }

        return cost;
    }

    public static int getOperationCost(EditOperation editOperation){
        int cost = -1;

        switch (editOperation){
            case Insert:
                cost = 1;
                break;
            case Delete:
                cost = 1;
                break;
            case Match:
                cost = 0;
                break;
            case Substitution:
                cost = 1;
                break;
        }

        return cost;
    }

    public static int getOperationMovement(EditOperation editOperation){
        int movement = -1;

        switch (editOperation){
            case Insert:
                movement = 0;
                break;
            case Delete:
                movement = 1;
                break;
            case Match:
                movement = 1;
                break;
            case Substitution:
                movement = 1;
                break;
        }

        return movement;
    }

    public static float getRankDiscount(int numberOfEdits){
        float operationDiscount = 1;
        for(int i = 1; i <= numberOfEdits; i++){
            operationDiscount *= getOperationDiscount(EditOperation.Insert, i);
        }

        return operationDiscount;
    }

    public static float getOperationDiscount(EditOperation editOperation, int previousEdits) {
        float discount = 1;
        switch (editOperation){
            case Insert:
                discount = 0.25f;
                break;
            case Delete:
                discount = 0.25f;
                break;
            case Match:
                discount = 1;
                break;
            case Substitution:
                discount = 0.25f;
                break;
        }

        return discount;
    }
}
