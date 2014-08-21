def bin_search(data, query):
    ln = len(query)
    if ln == 0:
        return -1
    left_anchor = 0
    right_anchor = len(data)
    flag = -1
    while True:
        if left_anchor >= right_anchor:
            return flag
        ind = (left_anchor + right_anchor)/2
        comp = data[ind]
        ln2 = len(comp)

        if query == comp:
            return ind
        if ln < ln2:
            comp = comp[:ln]
        if query == comp:
            flag = ind
        if ind == left_anchor or ind == right_anchor:
            return flag
        elif query > comp:
            left_anchor = ind
        else:
            right_anchor = ind
