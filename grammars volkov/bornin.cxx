#encoding "utf-8"
#GRAMMAR_ROOT L
Variable -> Noun<kwtype=variable>;
Strem -> "стремящемс";
Function -> Noun<kwtype=function> Variable "конец";
Num -> AnyWord<wff=/[1-9]?[0-9]{1,6}/>;

S -> "предел" Function interp(Predel.Function) "при" Variable interp(Predel.Variable) Strem "к" Num interp(Predel.To) "конец" "пред";

L -> (S interp(Fraction.Chislitel))(Num interp(Fraction.Chislitel)) "на" (S interp(Fraction.Znamenatel))(Num interp(Fraction.Znamenatel));

