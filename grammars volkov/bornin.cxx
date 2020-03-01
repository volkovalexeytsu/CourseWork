#encoding "utf-8"
#GRAMMAR_ROOT M 
//Variable -> Noun<kwtype=variable>;
//Strem -> "стремящемс";
//Function -> Noun<kwtype=function> Variable "конец" "функ";
Num -> AnyWord<wff=/[1-9]?[0-9]{1,6}/>;
Exp -> 'корень' 'из'  Exp  interp (Sqrt.Recursion) 'конец' 'подкорня';
Exp -> Num interp(Sqrt.Value);

//S -> "предел" Function interp(Predel.Function) "при" Variable interp(Predel.Variable) Strem "к" Num interp(Predel.To);

//L -> (S interp(Fraction.Chislitel))(Num interp(Fraction.Chislitel)) "на" (S interp(Fraction.Znamenatel))(Num interp(Fraction.Znamenatel));

M -> (Exp)".";
